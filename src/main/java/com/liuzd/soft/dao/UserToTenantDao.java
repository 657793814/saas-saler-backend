package com.liuzd.soft.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.entity.UserToTenantEntity;
import org.springframework.stereotype.Repository;

/**
 * saas_center.user_to_tenant dao类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@DS(GlobalConstant.DEFAULT_DB_KEY)
@Repository
public interface UserToTenantDao extends BaseMapper<UserToTenantEntity> {

}

