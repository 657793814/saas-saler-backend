package com.liuzd.soft.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuzd.soft.annotation.LogAnnotation;
import com.liuzd.soft.dao.TPermissionDao;
import com.liuzd.soft.dao.TRolePermissionDao;
import com.liuzd.soft.dao.TUserRoleDao;
import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.TRolePermissionDto;
import com.liuzd.soft.entity.TPermissionEntity;
import com.liuzd.soft.entity.TRolePermissionEntity;
import com.liuzd.soft.entity.TUserRoleEntity;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.PermissionService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.permission.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liuzd
 * @date: 2025/8/22
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    final TPermissionDao tPermissionDao;
    final TUserRoleDao tUserRoleDao;
    final TRolePermissionDao tRolePermissionDao;
    final UserServiceImpl userServiceImpl;


    public PermissionServiceImpl(TPermissionDao tPermissionDao, TUserRoleDao tUserRoleDao, TRolePermissionDao tRolePermissionDao, UserServiceImpl userServiceImpl) {
        this.tPermissionDao = tPermissionDao;

        this.tUserRoleDao = tUserRoleDao;
        this.tRolePermissionDao = tRolePermissionDao;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public List<TPermissionDto> all() {
        List<TPermissionEntity> list = tPermissionDao.selectList(new QueryWrapper<>());
        return list.stream().map(TPermissionDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TRolePermissionDto> queryPermissionByRole(Integer roleId) {
        List<TRolePermissionEntity> list = tRolePermissionDao.selectList(new QueryWrapper<TRolePermissionEntity>().eq("role_id", roleId));
        return list.stream().map(TRolePermissionDto::new).collect(Collectors.toList());
    }

    /**
     * 分页查询一级节点
     * 然后根据一级节点查找所有子节点，合并返回
     *
     * @return
     */
    @LogAnnotation
    @Override
    public PageResult<TPermissionDto> pageQuery(PermissionPageReq pageReq) {
        Page<TPermissionEntity> page = new Page<>(pageReq.getCurrent(), pageReq.getSize());
        QueryWrapper<TPermissionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_code", "");
        if (StringUtils.isNotBlank(pageReq.getCode())) {
            queryWrapper.like("code", pageReq.getCode());
        }
        if (StringUtils.isNotBlank(pageReq.getName())) {
            queryWrapper.like("name", pageReq.getName());
        }
        Page<TPermissionEntity> resultPage = tPermissionDao.selectPage(page, queryWrapper);
        PageResult<TPermissionDto> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        List<TPermissionEntity> list = resultPage.getRecords();

        //查询下面的子节点
        List<String> parentCodes = list.stream().map(TPermissionEntity::getCode).collect(Collectors.toList());
        QueryWrapper<TPermissionEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("parent_code", parentCodes);
        List<TPermissionEntity> childrenList = tPermissionDao.selectList(queryWrapper1);
        if (CollUtil.isNotEmpty(childrenList)) {
            list.addAll(childrenList);
        }
        pageResult.setRecords(list.stream().map(TPermissionDto::new).collect(Collectors.toList()));
        return pageResult;
    }

    @Override
    public void add(AddPermissionReq addReq) {
        TPermissionEntity tPermissionEntity = new TPermissionEntity();
        tPermissionEntity.setCode(addReq.getCode());
        tPermissionEntity.setName(addReq.getName());
        tPermissionEntity.setPath(addReq.getPath());
        tPermissionEntity.setIcon(addReq.getIcon());
        tPermissionEntity.setParentCode(addReq.getParentCode());
        tPermissionEntity.setEnable(addReq.getEnable());
        tPermissionDao.insert(tPermissionEntity);
    }

    @LogAnnotation
    @Override
    public void edit(EditPermissionReq editReq) {
        QueryWrapper<TPermissionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", editReq.getId());
        TPermissionEntity tPermissionEntity = tPermissionDao.selectOne(queryWrapper);
        Assert.notNull(tPermissionEntity, () -> MyException.exception(RetEnums.PERMISSION_NOT_EXIST));

        tPermissionEntity.setName(editReq.getName());
        tPermissionEntity.setCode(editReq.getCode());
        tPermissionEntity.setPath(editReq.getPath());
        tPermissionEntity.setIcon(editReq.getIcon());
        tPermissionEntity.setParentCode(editReq.getParentCode());
        tPermissionEntity.setEnable(editReq.getEnable());
        tPermissionDao.update(tPermissionEntity, queryWrapper);

    }

    @LogAnnotation
    @Override
    public void del(DelPermissionReq delPermissionReq) {
        if (!userServiceImpl.checkCurrentUserIsAdmin()) {
            throw MyException.exception(RetEnums.NO_PERMISSION);
        }
        tPermissionDao.delete(new QueryWrapper<TPermissionEntity>().eq("id", delPermissionReq.getId()));
    }

    @LogAnnotation
    @Override
    public void editRolePermission(EditRolePermissionReq req) {
        if (!userServiceImpl.checkCurrentUserIsAdmin()) {
            throw MyException.exception(RetEnums.NO_PERMISSION);
        }

        //清空权限
        if (CollUtil.isEmpty(req.getPermissionIds())) {
            tUserRoleDao.delete(new QueryWrapper<TUserRoleEntity>().eq("role_id", req.getRoleId()));
        }

        //获取当前角色权限点，对比更新
        List<TRolePermissionEntity> list = tRolePermissionDao.selectList(new QueryWrapper<TRolePermissionEntity>().eq("role_id", req.getRoleId()));
        List<Integer> oldPermissionIds = list.stream().map(TRolePermissionEntity::getPermissionId).collect(Collectors.toList());

        List<Integer> newPermissionIds = req.getPermissionIds();
        List<Integer> addPermissionIds = newPermissionIds.stream().filter(permissionId -> !oldPermissionIds.contains(permissionId)).collect(Collectors.toList());
        List<Integer> delPermissionIds = oldPermissionIds.stream().filter(permissionId -> !newPermissionIds.contains(permissionId)).collect(Collectors.toList());
        log.info("addPermissionIds:{}", addPermissionIds);
        log.info("delPermissionIds:{}", delPermissionIds);
        if (CollUtil.isNotEmpty(addPermissionIds)) {
            for (Integer permissionId : addPermissionIds) {
                TRolePermissionEntity entity = new TRolePermissionEntity();
                entity.setRoleId(req.getRoleId());
                entity.setPermissionId(permissionId);
                tRolePermissionDao.insert(entity);
            }
        }

        if (CollUtil.isNotEmpty(delPermissionIds)) {
            tRolePermissionDao.delete(new QueryWrapper<TRolePermissionEntity>().eq("role_id", req.getRoleId()).in("permission_id", delPermissionIds));
        }

    }
}
