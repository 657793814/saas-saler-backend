package com.liuzd.soft.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuzd.soft.annotation.LogAnnotation;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.dao.TPermissionDao;
import com.liuzd.soft.dao.TRoleDao;
import com.liuzd.soft.dao.TRolePermissionDao;
import com.liuzd.soft.dao.TUserRoleDao;
import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.TRoleDto;
import com.liuzd.soft.entity.TPermissionEntity;
import com.liuzd.soft.entity.TRoleEntity;
import com.liuzd.soft.entity.TRolePermissionEntity;
import com.liuzd.soft.entity.TUserRoleEntity;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.RolesService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.role.AddRoleReq;
import com.liuzd.soft.vo.role.EditRoleReq;
import com.liuzd.soft.vo.role.RolePageReq;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: liuzd
 * @date: 2025/8/22
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@Slf4j
public class RolesServiceImpl implements RolesService {

    final TPermissionDao tPermissionDao;
    final TUserRoleDao tUserRoleDao;
    final TRolePermissionDao tRolePermissionDao;
    final TRoleDao tRoleDao;

    public RolesServiceImpl(TPermissionDao tPermissionDao, TUserRoleDao tUserRoleDao, TRolePermissionDao tRolePermissionDao, TRoleDao tRoleDao) {
        this.tPermissionDao = tPermissionDao;
        this.tUserRoleDao = tUserRoleDao;
        this.tRolePermissionDao = tRolePermissionDao;
        this.tRoleDao = tRoleDao;
    }


    @LogAnnotation
    public List<TPermissionDto> queryUserMenus(String openid) {
        List<TPermissionDto> list = new ArrayList<>();
        //1. 查询用户的角色
        List<TUserRoleEntity> userRoles = queryUserRoles(openid);
        if (CollUtil.isEmpty(userRoles)) {
            return list;
        }

        //2. 查询角色数据
        List<Integer> roleIds = userRoles.stream().map(TUserRoleEntity::getRoleId).collect(Collectors.toList());
        List<TRoleEntity> roles = queryUserRoles(roleIds);

        //3. 根据角色查询权限点
        List<Integer> permissionIds;
        List<TRolePermissionEntity> rolePermissions;
        List<TRolePermissionEntity> defaultRolePermissions;
        List<TRolePermissionEntity> mergedList;

        roleIds = roles.stream().map(TRoleEntity::getId).collect(Collectors.toList());
        rolePermissions = queryUserPermissionsByRoles(roleIds);
        if (roleIds.contains(GlobalConstant.DEFAULT_ADMIN_ROLE_ID)) {  // 判断是否是管理员角色
            defaultRolePermissions = queryAllPermissionsByAdmin();
            mergedList = Stream.concat(
                    rolePermissions.stream(),
                    defaultRolePermissions.stream()
            ).collect(Collectors.toList());
        } else {
            if (CollUtil.isEmpty(rolePermissions)) {
                return list;
            }
            mergedList = rolePermissions;
        }
        permissionIds = mergedList.stream().map(TRolePermissionEntity::getPermissionId).distinct().collect(Collectors.toList());

        //4. 根据权限点集合查询权限数据
        List<TPermissionEntity> permissionEntities = queryUserPermissions(permissionIds);
        if (CollUtil.isEmpty(permissionEntities)) {
            return list;
        }


        //5. 整合权限点数据
        //权限点 id->角色ID集合
        Map<Integer, List<Integer>> permissionRoleMap = mergedList.stream()
                .collect(Collectors.groupingBy(
                        TRolePermissionEntity::getPermissionId,
                        Collectors.mapping(TRolePermissionEntity::getRoleId, Collectors.toList())  // 角色ID集合作为value
                ));

        //role id->name 映射
        Map<Integer, String> roleMap = roles.stream()
                .collect(Collectors.toMap(TRoleEntity::getId, TRoleEntity::getName));

        return handlePermissionData(permissionEntities, permissionRoleMap, roleMap);
    }

    public List<TPermissionDto> handlePermissionData(List<TPermissionEntity> permissionEntities, Map<Integer, List<Integer>> permissionRoleMap, Map<Integer, String> roleMap) {
        List<TPermissionDto> list = new ArrayList<>();
        ConcurrentHashMap<String, TPermissionDto> map = new ConcurrentHashMap<>();
        for (TPermissionEntity permissionEntity : permissionEntities) {
            TPermissionDto permissionDto = new TPermissionDto();
            permissionDto.setCode(permissionEntity.getCode());
            permissionDto.setName(permissionEntity.getName());
            permissionDto.setPath(permissionEntity.getPath());
            permissionDto.setIcon(permissionEntity.getIcon());

            //处理roles
            List<Integer> roleIds = permissionRoleMap.get(permissionEntity.getId());
            if (CollUtil.isEmpty(roleIds)) {
                log.info("权限点：{},code:{},没有授予任何角色。", permissionEntity.getName(), permissionEntity.getCode());
                continue;
            }

            //添加admin并去重,任何权限点的角色都要加入admin
            List<String> roleNames = Stream.concat(
                            roleIds.stream()
                                    .map(roleId -> roleMap.get(roleId))
                                    .filter(Objects::nonNull),  // 过滤掉null值
                            Stream.of(GlobalConstant.DEFAULT_ADMIN_ROLE)  // 添加admin角色
                    )
                    .distinct()  // 去重
                    .collect(Collectors.toList());
            permissionDto.setRoles(roleNames);

            //处理父子节点
            List<TPermissionDto> children = new ArrayList<>();
            if (StringUtil.isNotBlank(permissionEntity.getParentCode()) && map.containsKey(permissionEntity.getParentCode())) {
                children = map.get(permissionEntity.getParentCode()).getChildren();
                if (CollUtil.isEmpty(children)) {
                    children = new ArrayList<>();
                    // 修改父节点的path 为第一个children的path
                    map.get(permissionEntity.getParentCode()).setPath(permissionEntity.getPath());
                }
                permissionDto.setChildren(new ArrayList<>());
                children.add(permissionDto);
                map.get(permissionEntity.getParentCode()).setChildren(children);
            } else {
                permissionDto.setChildren(children);
                map.put(permissionEntity.getCode(), permissionDto);
            }
        }
        list = map.values().stream().collect(Collectors.toList());
        return list;
    }

    public List<TUserRoleEntity> queryUserRoles(String openid) {
        QueryWrapper<TUserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return tUserRoleDao.selectList(queryWrapper);
    }

    public List<TRolePermissionEntity> queryUserPermissionsByRoles(List<Integer> roleIds) {
        QueryWrapper<TRolePermissionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        return tRolePermissionDao.selectList(queryWrapper);
    }

    public List<TRolePermissionEntity> queryAllPermissionsByAdmin() {
        QueryWrapper<TPermissionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable", 1);
        List<TPermissionEntity> permissionEntities = tPermissionDao.selectList(queryWrapper);
        List<TRolePermissionEntity> list = new ArrayList<>();
        for (TPermissionEntity permissionEntity : permissionEntities) {
            TRolePermissionEntity rolePermissionEntity = new TRolePermissionEntity();
            rolePermissionEntity.setRoleId(GlobalConstant.DEFAULT_ADMIN_ROLE_ID);
            rolePermissionEntity.setPermissionId(permissionEntity.getId());
            list.add(rolePermissionEntity);
        }
        return list;
    }

    public List<TPermissionEntity> queryUserPermissions(List<Integer> ids) {
        List<TPermissionEntity> list = new ArrayList<>();
        QueryWrapper<TPermissionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable", 1).in("id", ids);
        return tPermissionDao.selectList(queryWrapper);
    }

    public List<TRoleEntity> queryUserRoles(List<Integer> ids) {
        QueryWrapper<TRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable", 1).in("id", ids);
        return tRoleDao.selectList(queryWrapper);
    }

    @Override
    public PageResult<TRoleDto> pageQuery(RolePageReq pageRequest) {
        Page<TRoleEntity> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        QueryWrapper<TRoleEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotBlank(pageRequest.getName())) {
            queryWrapper.like("name", pageRequest.getName());
        }
        if (Objects.nonNull(pageRequest.getEnable())) {
            queryWrapper.eq("enable", pageRequest.getEnable() ? 1 : 0);
        }
        Page<TRoleEntity> resultPage = tRoleDao.selectPage(page, queryWrapper);
        PageResult<TRoleDto> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords().stream().map(TRoleDto::new).collect(Collectors.toList()));
        return pageResult;
    }

    @Override
    public void addRole(AddRoleReq addRoleReq) {
        TRoleEntity tRoleEntity = new TRoleEntity();
        tRoleEntity.setName(addRoleReq.getName());
        tRoleEntity.setDesc(addRoleReq.getDesc());
        tRoleEntity.setEnable(addRoleReq.getEnable());
        tRoleDao.insert(tRoleEntity);
    }

    @Override
    public void editRole(EditRoleReq editRoleReq) {
        QueryWrapper<TRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", editRoleReq.getId());
        TRoleEntity tRoleEntity = tRoleDao.selectOne(queryWrapper);
        Assert.notNull(tRoleEntity, () -> MyException.exception(RetEnums.ROLE_NOT_EXIST));

        tRoleEntity.setDesc(editRoleReq.getDesc());
        tRoleEntity.setEnable(editRoleReq.getEnable());
        if (editRoleReq.getId() == GlobalConstant.DEFAULT_ADMIN_ROLE_ID) {
            tRoleEntity.setEnable(1); //内置管理员不能被禁用
        }
        tRoleDao.update(tRoleEntity, queryWrapper);
    }
}
