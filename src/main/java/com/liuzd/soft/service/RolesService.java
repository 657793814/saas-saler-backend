package com.liuzd.soft.service;

import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.TRoleDto;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.role.AddRoleReq;
import com.liuzd.soft.vo.role.EditRoleReq;
import com.liuzd.soft.vo.role.RolePageReq;

import java.util.List;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface RolesService {

    List<TPermissionDto> queryUserMenus(String openid);

    PageResult<TRoleDto> pageQuery(RolePageReq pageRequest);

    void addRole(AddRoleReq addRoleReq);

    void editRole(EditRoleReq editRoleReq);

}
