package com.liuzd.soft.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.dto.TenantDataSourceDto;
import com.liuzd.soft.entity.TenantsEntity;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.TenantsService;
import jakarta.validation.constraints.NotNull;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * 自定义动态数据源，基于运行时动态租户切换的动态数据源
 * 请参考：
 * org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource  动态路由数据源切换的基类，建议阅读其源码
 * com.baomidou.dynamic.datasource.DynamicRoutingDataSource
 * <p>
 * 注意：AbstractRoutingDataSource 只支持单库事务，切换数据源是在开启事务之前执行。
 * Spring使用 DataSourceTransactionManager进行事务管理。开启事务，会将数据源缓存到DataSourceTransactionObject对象中，后续的commit和 rollback事务操作实际上是使用的同一个数据源。
 * 所以 @DSTransactional 这种情况下，会使 @DS 失效，造成无法切库. 请阅读 org.springframework.jdbc.datasource.DataSourceTransactionManager#doBegin(java.lang.Object, org.springframework.transaction.TransactionDefinition)
 * <p>
 * @author liuzd01
 * @email:liuzd2025@qq.com
 */
@Slf4j
@Data
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Autowired
    @Lazy
    private TenantsService tenantsService;

    private TenantsEntity tenantsEntity;

    @Value("${spring.datasource.dynamic.datasource.default.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.default.username}")
    private String userName;

    @Value("${spring.datasource.dynamic.datasource.default.password}")
    private String password;

    /**
     * 用于保存租户key和数据源的映射关系，目标数据源map的拷贝
     */
    public Map<Object, Object> backupTargetDataSources;

    /**
     * 静态数据源缓存
     */
    protected Map<String, DataSource> staticDatasourceMap;

    /**
     * 写的时候每个key一把锁，降低锁的粒度
     */
    protected final Map<String, Lock> keyLockMap = new ConcurrentHashMap<>();

    /**
     * 必须实现其方法
     * 动态数据源类集成了Spring提供的AbstractRoutingDataSource类，AbstractRoutingDataSource
     * 中获取数据源的方法就是 determineTargetDataSource，而此方法又通过 determineCurrentLookupKey 方法获取查询数据源的key
     * 通过key在resolvedDataSources这个map中获取对应的数据源，resolvedDataSources的值是由afterPropertiesSet()这个方法从
     * TargetDataSources获取的
     *
     * @return 数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.peek();
    }

    /**
     * 动态数据源构造器
     *
     * @param defaultDataSource 默认数据源
     * @param targetDataSource  目标数据源映射
     */
    public DynamicDataSource(DataSource defaultDataSource, Map<Object, Object> targetDataSource) {
        backupTargetDataSources = targetDataSource;
        super.setDefaultTargetDataSource(defaultDataSource);
        // 存放数据源的map
        super.setTargetDataSources(backupTargetDataSources);
        // afterPropertiesSet 的作用很重要，它负责解析成可用的目标数据源
        super.afterPropertiesSet();
    }


    /**
     * 添加数据源到目标数据源map中
     *
     * @param tenantDataSourceDto 租户数据源实体
     */
    public void addDataSource(TenantDataSourceDto tenantDataSourceDto) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(tenantDataSourceDto.getUrl());
        druidDataSource.setUsername(tenantDataSourceDto.getUsername());
        druidDataSource.setPassword(tenantDataSourceDto.getPassword());

        // 将传入的数据源对象放入动态数据源类的静态map中，然后再讲静态map重新保存进动态数据源中
        backupTargetDataSources.put(tenantDataSourceDto.getTenantKey(), druidDataSource);
        super.setTargetDataSources(backupTargetDataSources);
        super.afterPropertiesSet();
    }

    /**
     * 是否存在租户数据源
     *
     * @param tenantKey 租户key
     */
    public Boolean existTenantKey(String tenantKey) {
        if (StringUtil.isBlank(tenantKey)) {
            return false;
        }
        return backupTargetDataSources.containsKey(tenantKey);
    }

    /**
     * 根据租户ID切换DB数据源 并返回租户code
     *
     * @param tenantId 租户ID
     * @return tenantCode 租户code
     */
    public String switchTenantByTenantId(@NotNull String tenantId) {
        //获取租户信息
        Assert.notNull(tenantsService, () -> MyException.exception(RetEnums.BUSINESS_ERROR, "tenantsService未注入"));

        tenantsEntity = tenantsService.findByTenantId(tenantId);

        Assert.notNull(tenantsEntity, () -> MyException.exception(RetEnums.TENANT_NOT_EXISTS));
        Assert.notNull(tenantsEntity.getTenantCode(), () -> MyException.exception(RetEnums.TENANT_NOT_EXISTS));
        //租户禁用不执行操作
        Assert.isTrue(tenantsEntity.getIsEnable() == 1, () -> MyException.exception(RetEnums.TENANT_DISABLE));

        switchTenant(tenantsEntity.getTenantCode());
        return tenantsEntity.getTenantCode();
    }

    /**
     * 切换租户
     *
     * @param dsKey 数据源key(目前用的值tenantCode,不是用的tenantId)
     *              场景1：具体的租户code , eg: dev
     *              场景2：具体的静态库key ,eg: GlobalConstant.DEFAULT_DB_KEY , GlobalConstant.LOG_DB_KEY
     *              实际运行中，只有切换租户库调用此函数（请求拦截，运行时动态切换），配置库通过@DS注解完成
     */
    public void switchTenant(@NotNull String dsKey) {
        if (StringUtil.isBlank(dsKey)) {
            log.error("切换租户异常，入参异常");
        }

        if (dsKey.equals(DynamicDataSourceContextHolder.peek())) {
            log.info("当前租户正在使用相同数据源，不用切换，租户code:{}", dsKey);
            return;
        }

        if (existTenantKey(dsKey)) {
            log.info("当前租户数据源已经加载过，正在切换， dsKey:{}, 原数据源:{}", dsKey, DynamicDataSourceContextHolder.peek());
            DynamicDataSourceContextHolder.push(dsKey);  //直接切换
            return;
        }

        /*************** 动态运行时，首次加载某个租户的数据源 ********************/
        //根据租户code查询数据
        TenantDataSourceDto tenantsDto = new TenantDataSourceDto();
        TenantsEntity info = getTenantsEntity();  // switchTenantByTenantId()已经查询过，这里不用再查询
        if (ObjectUtil.isEmpty(info)) {

            Assert.notNull(tenantsService, () -> MyException.exception(RetEnums.BUSINESS_ERROR, "tenantsService未注入"));
            info = tenantsService.findByTenantCode(dsKey);
        }

        Assert.notNull(info, () -> MyException.exception(RetEnums.TENANT_NOT_EXISTS, "租户不存在，切换DB数据源失败"));
        Assert.isTrue(info.getIsEnable() == 1, () -> MyException.exception(RetEnums.TENANT_DISABLE));

        tenantsDto.setTenantKey(info.getTenantCode());  //tenantCode 作为数据源的标识
        tenantsDto.setUrl(url.replace(GlobalConstant.CONFIG_DB_NAME, info.getDbName())); //替换链接
        tenantsDto.setUsername(userName);
        tenantsDto.setPassword(password);

        //添加数据源
        addDataSource(tenantsDto);
        DynamicDataSourceContextHolder.push(dsKey); //以租户code为key
        log.info("将{}库 以{}为key加入动态数据源对象中", info.getDbName(), info.getTenantCode());

    }

    /**
     * 切换到默认config库
     * 目前config是唯一确定的，可以通过  @DS(GlobalConstant.DEFAULT_DB_KEY) 实现 mapper的自动切换，也可以通过此方法实现自动切换
     */
    public void switchDefaultTenant() {
        if (GlobalConstant.DEFAULT_DB_KEY.equals(DynamicDataSourceContextHolder.peek())) {
            return;
        }
        switchTenant(GlobalConstant.DEFAULT_DB_KEY);
    }

}
