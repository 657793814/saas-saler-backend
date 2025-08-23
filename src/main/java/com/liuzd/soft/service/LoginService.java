package com.liuzd.soft.service;

import com.liuzd.soft.vo.login.*;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface LoginService {

    LoginResp<LoginUserRet> doLogin(LoginReq loginReq);

    void unLogin(UnLoginReq unLoginReq);

    LoginResp<LoginUserRet> refreshToken(RefreshTokenReq refreshTokenReq);
    

}
