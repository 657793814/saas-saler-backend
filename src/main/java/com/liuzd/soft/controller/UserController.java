package com.liuzd.soft.controller;

import cn.hutool.core.lang.Assert;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dto.TPermissionDto;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.entity.TUserEntity;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.RolesService;
import com.liuzd.soft.service.UserService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.rets.ResultMessage;
import com.liuzd.soft.vo.user.AddUserReq;
import com.liuzd.soft.vo.user.EditUserReq;
import com.liuzd.soft.vo.user.UserPageReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/12
 * @email: liuzd2025@qq.com
 * @desc
 */
@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {
    final UserService userService;
    final RolesService rolesService;

    @PostMapping("/page")
    public ResultMessage<PageResult<TUserEntity>> pageQuery(@RequestBody UserPageReq userPageReq) {
        return ResultMessage.success(userService.pageQuery(userPageReq));
    }

    @PostMapping("/add")
    public ResultMessage<Object> addUser(@Valid @RequestBody AddUserReq addUserReq) {
        userService.addUser(addUserReq);
        return ResultMessage.success("success");
    }

    @PostMapping("/edit")
    public ResultMessage<Object> editUser(@Valid @RequestBody EditUserReq editUserReq) {
        userService.editUser(editUserReq);
        return ResultMessage.success("success");
    }

    @PostMapping("/menus")
    public ResultMessage<List<TPermissionDto>> menus() {
        TokenInfo tokenInfo = (TokenInfo) ThreadContextHolder.get(GlobalConstant.LOGIN_USER_INFO);
        Assert.notNull(tokenInfo, () -> MyException.exception(RetEnums.LOGIN_EXPIRE));
        return ResultMessage.success(rolesService.queryUserMenus(tokenInfo.getOpenid()));
    }


}
