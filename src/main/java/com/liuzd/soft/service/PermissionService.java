package com.liuzd.soft.service;

import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.TRolePermissionDto;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.permission.*;

import java.util.List;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface PermissionService {


    PageResult<TPermissionDto> pageQuery(PermissionPageReq pageRequest);

    List<TPermissionDto> all();

    List<TRolePermissionDto> queryPermissionByRole(Integer roleId);

    void add(AddPermissionReq addRoleReq);

    void edit(EditPermissionReq editRoleReq);

    void del(DelPermissionReq delPermissionReq);

    void editRolePermission(EditRolePermissionReq req);

}
