package com.liuzd.soft.controller;

import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.TRolePermissionDto;
import com.liuzd.soft.service.PermissionService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.permission.*;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@RestController
@RequestMapping(path = "/permissions")
@RequiredArgsConstructor
public class PermissionController {

    final PermissionService permissionService;

    @PostMapping("/page")
    public ResultMessage<PageResult<TPermissionDto>> pageQuery(@RequestBody PermissionPageReq pageReq) {
        return ResultMessage.success(permissionService.pageQuery(pageReq));
    }

    @PostMapping("/all")
    public ResultMessage<List<TPermissionDto>> all() {
        return ResultMessage.success(permissionService.all());
    }

    @PostMapping("/by_role")
    public ResultMessage<List<TRolePermissionDto>> queryPermissionByRole(@RequestParam("role_id") Integer roleId) {
        return ResultMessage.success(permissionService.queryPermissionByRole(roleId));
    }

    @PostMapping("/add")
    public ResultMessage<Object> add(@Valid @RequestBody AddPermissionReq addReq) {
        permissionService.add(addReq);
        return ResultMessage.success("success");
    }

    @PostMapping("/edit")
    public ResultMessage<Object> edit(@Valid @RequestBody EditPermissionReq editReq) {
        permissionService.edit(editReq);
        return ResultMessage.success("success");
    }

    @PostMapping("/del")
    public ResultMessage<Object> del(@Valid @RequestBody DelPermissionReq delReq) {
        permissionService.del(delReq);
        return ResultMessage.success("success");
    }


    @PostMapping("/edit_role_permission")
    public ResultMessage<Object> editRolePermission(@Valid @RequestBody EditRolePermissionReq req) {
        permissionService.editRolePermission(req);
        return ResultMessage.success("success");
    }


}
