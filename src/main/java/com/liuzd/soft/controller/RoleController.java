package com.liuzd.soft.controller;

import com.liuzd.soft.dto.TRoleDto;
import com.liuzd.soft.service.RolesService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.rets.ResultMessage;
import com.liuzd.soft.vo.role.AddRoleReq;
import com.liuzd.soft.vo.role.EditRoleReq;
import com.liuzd.soft.vo.role.RolePageReq;
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
@RequestMapping(path = "/role")
@RequiredArgsConstructor
public class RoleController {

    final RolesService rolesService;

    @PostMapping("/page")
    public ResultMessage<PageResult<TRoleDto>> pageQuery(@RequestBody RolePageReq rolePageReq) {
        return ResultMessage.success(rolesService.pageQuery(rolePageReq));
    }

    @PostMapping("/add")
    public ResultMessage<Object> add(@Valid @RequestBody AddRoleReq addRoleReq) {
        rolesService.addRole(addRoleReq);
        return ResultMessage.success("success");
    }

    @PostMapping("/edit")
    public ResultMessage<Object> edit(@Valid @RequestBody EditRoleReq editRoleReq) {
        rolesService.editRole(editRoleReq);
        return ResultMessage.success("success");
    }


}
