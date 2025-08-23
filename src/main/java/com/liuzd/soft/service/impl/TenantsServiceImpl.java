package com.liuzd.soft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuzd.soft.dao.TenantsDao;
import com.liuzd.soft.entity.TenantsEntity;
import com.liuzd.soft.service.TenantsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantsServiceImpl extends ServiceImpl<TenantsDao, TenantsEntity> implements TenantsService {

    private final TenantsDao tenantsDao;

    @Override
    public TenantsEntity findByTenantId(String tenantId) {
        QueryWrapper<TenantsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        return tenantsDao.selectOne(queryWrapper);
    }

    @Override
    public TenantsEntity findByTenantCode(String tenantCode) {
        QueryWrapper<TenantsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_code", tenantCode);
        return tenantsDao.selectOne(queryWrapper);
    }
}
