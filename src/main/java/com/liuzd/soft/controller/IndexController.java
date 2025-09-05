package com.liuzd.soft.controller;

import com.liuzd.soft.service.LoginService;
import com.liuzd.soft.service.impl.ServiceCall;
import com.liuzd.soft.vo.login.LoginReq;
import com.liuzd.soft.vo.login.RefreshTokenReq;
import com.liuzd.soft.vo.login.UnLoginReq;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = "/")
@RequiredArgsConstructor
public class IndexController {

    final LoginService loginService;
    final ServiceCall serviceCall;

    @RequestMapping(path = "/hello_world")
    public String helloWorld(HttpServletRequest request) {
        return request.getServerPort() + " helloWorld";
    }

    @RequestMapping(path = "/call_service")
    public ResultMessage<Object> callService() {
        //http://localhost:8080/call_service
        return ResultMessage.success(serviceCall.callService("saas-saler-admin-prod", "/hello_world"));
    }
    

    @RequestMapping(path = "/refresh_token")
    public ResultMessage<Object> refreshToken(
            @RequestBody RefreshTokenReq refreshTokenReq
    ) {

        return ResultMessage.success(loginService.refreshToken(refreshTokenReq));
    }


    @RequestMapping(path = "/login")
    public ResultMessage<Object> login(@RequestBody LoginReq loginReq) {
        return ResultMessage.success(loginService.doLogin(loginReq));
    }

    @RequestMapping(path = "/un_login")
    public ResultMessage<Object> unLogin(@RequestBody UnLoginReq unLoginReq) {
        loginService.unLogin(unLoginReq);
        return ResultMessage.success("unLogin success");
    }

}
