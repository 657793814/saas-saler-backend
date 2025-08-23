package com.liuzd.soft.service;

import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.permission.AddPermissionReq;
import com.liuzd.soft.vo.permission.DelPermissionReq;
import com.liuzd.soft.vo.permission.EditPermissionReq;
import com.liuzd.soft.vo.permission.PermissionPageReq;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface PermissionService {


    PageResult<TPermissionDto> pageQuery(PermissionPageReq pageRequest);

    void add(AddPermissionReq addRoleReq);

    void edit(EditPermissionReq editRoleReq);

    void del(DelPermissionReq delPermissionReq);

}
