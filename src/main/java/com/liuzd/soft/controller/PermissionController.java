package com.liuzd.soft.controller;

import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.service.PermissionService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.permission.AddPermissionReq;
import com.liuzd.soft.vo.permission.DelPermissionReq;
import com.liuzd.soft.vo.permission.EditPermissionReq;
import com.liuzd.soft.vo.permission.PermissionPageReq;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
