package com.liuzd.soft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuzd.soft.entity.TenantsEntity;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface TenantsService extends IService<TenantsEntity> {

    TenantsEntity findByTenantId(String tenantId);

    TenantsEntity findByTenantCode(String tenantCode);

}
