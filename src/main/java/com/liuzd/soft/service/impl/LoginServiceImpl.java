package com.liuzd.soft.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuzd.soft.config.DynamicDataSource;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dao.PUserToTenantDao;
import com.liuzd.soft.dao.TUserDao;
import com.liuzd.soft.dao.TenantsDao;
import com.liuzd.soft.dto.token.RefreshTokenInfo;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.dto.token.UserTokenInfo;
import com.liuzd.soft.entity.PUserToTenantEntity;
import com.liuzd.soft.entity.TUserEntity;
import com.liuzd.soft.entity.TenantsEntity;
import com.liuzd.soft.enums.LoginTypeEnum;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.LoginService;
import com.liuzd.soft.utils.SecureMd5Utils;
import com.liuzd.soft.utils.TokenUtils;
import com.liuzd.soft.vo.login.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RedisTemplate redisTemplate;
    private final DynamicDataSource dynamicDataSource;
    private final TUserDao tUserDao;
    private final PUserToTenantDao pUserToTenantDao;
    private final TenantsDao tenantsDao;

    @Override
    public LoginResp<LoginUserRet> doLogin(LoginReq loginReq) {

        //1. 根据租户code查询租户信息
        dynamicDataSource.switchTenant(ThreadContextHolder.getTenantCode());
        QueryWrapper<TUserEntity> queryWrapper = new QueryWrapper<>();
        if (loginReq.getLoginType() == LoginTypeEnum.LOGIN_TYPE_USERNAME.getCode()) {
            queryWrapper.eq("uname", loginReq.getUsername());
        } else if (loginReq.getLoginType() == LoginTypeEnum.LOGIN_TYPE_MOBILE.getCode()) {
            queryWrapper.eq("mobile", loginReq.getMobile());
        } else {
            throw MyException.exception(RetEnums.UNKNOWN_LOGIN_TYPE);
        }
        TUserEntity tUserEntity = tUserDao.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(tUserEntity)) {
            throw MyException.exception(RetEnums.USER_NOT_EXIST);
        }

        //2. 验证用户
        if (loginReq.getLoginType() == LoginTypeEnum.LOGIN_TYPE_USERNAME.getCode()) {
            if (!tUserEntity.getPassword().equals(SecureMd5Utils.md5WithSalt(loginReq.getPassword(), tUserEntity.getSalt()))) {
                throw MyException.exception(RetEnums.USERNAME_OR_PWD_ERROR);
            }
        } else if (loginReq.getLoginType() == LoginTypeEnum.LOGIN_TYPE_MOBILE.getCode()) {
            if (!GlobalConstant.TEST_SMS_CODE.equals(loginReq.getSmsCode())) {
                throw MyException.exception(RetEnums.USERNAME_OR_PWD_ERROR, "验证码错误");
            }
        }

        PUserToTenantEntity PUserToTenantEntity = checkAndGetUserInfo(tUserEntity);

        //3. 生成token等并存储到redis中
        String token = TokenUtils.generateToken();
        String refreshToken = TokenUtils.generateRefreshToken();
        LoginResp<LoginUserRet> loginResp = genToken(refreshToken, ThreadContextHolder.getTenantCode(), tUserEntity.getOpenid(), token, tUserEntity);
        return getLoginUserRetLoginResp(tUserEntity, PUserToTenantEntity, loginResp);
    }

    @Override
    public void unLogin(UnLoginReq unLoginReq) {
        TokenInfo tokenInfo = getTokenInfo(unLoginReq.getToken());
        UserTokenInfo userTokenInfo = getUserTokenInfo(tokenInfo.getOpenid());

        delToken(unLoginReq.getToken());
        delRefreshToken(userTokenInfo.getRefreshToken());
        delUserTokenInfo(tokenInfo.getOpenid());
    }

    @Override
    public LoginResp<LoginUserRet> refreshToken(RefreshTokenReq refreshTokenReq) {
        String refreshToken = refreshTokenReq.getRefreshToken();
        String openid = refreshTokenReq.getOpenid();
        String tenantCode = refreshTokenReq.getTenantCode();
        Assert.notBlank(refreshToken, () -> MyException.exception(RetEnums.PARAMETER_NOT_VALID, "refresh_token参数异常"));
        Assert.notBlank(openid, () -> MyException.exception(RetEnums.PARAMETER_NOT_VALID, "openid参数异常"));
        Assert.notBlank(tenantCode, () -> MyException.exception(RetEnums.PARAMETER_NOT_VALID, "tenant_code参数异常"));

        RefreshTokenInfo refreshTokenInfo = getRefreshTokenInfo(refreshToken);
        Assert.notNull(refreshTokenInfo, () -> MyException.exception(RetEnums.PARAMETER_NOT_VALID, "refresh_token已失效"));

        if (!openid.equals(refreshTokenInfo.getOpenid()) ||
                !tenantCode.equals(refreshTokenInfo.getTenantCode())
        ) {
            throw MyException.exception(RetEnums.PARAMETER_NOT_VALID, "refresh_token验证失败");
        }

        dynamicDataSource.switchTenant(tenantCode);
        QueryWrapper<TUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        TUserEntity tUserEntity = tUserDao.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(tUserEntity)) {
            throw MyException.exception(RetEnums.USER_NOT_EXIST);
        }

        PUserToTenantEntity PUserToTenantEntity = checkAndGetUserInfo(tUserEntity);

        String oldToken = refreshTokenInfo.getToken();
        delToken(oldToken);
        delRefreshToken(refreshToken);

        String newToken = TokenUtils.generateToken();
        LoginResp<LoginUserRet> loginResp = genToken(refreshToken, refreshTokenInfo.getTenantCode(), refreshTokenInfo.getOpenid(), newToken, tUserEntity);

        return getLoginUserRetLoginResp(tUserEntity, PUserToTenantEntity, loginResp);
    }

    private LoginResp<LoginUserRet> getLoginUserRetLoginResp(TUserEntity tUserEntity, PUserToTenantEntity PUserToTenantEntity, LoginResp<LoginUserRet> loginResp) {
        LoginUserRet loginUserRet = new LoginUserRet();
        loginUserRet.setMobile(tUserEntity.getMobile());
        loginUserRet.setOpenid(tUserEntity.getOpenid());
        loginUserRet.setUserCode(tUserEntity.getUserCode());
        loginUserRet.setUname(tUserEntity.getUserName());
        loginUserRet.setEnable(tUserEntity.getEnable());
        loginUserRet.setUnionId(PUserToTenantEntity.getUnionId());
        loginUserRet.setTenantCode(ThreadContextHolder.getTenantCode());
        loginResp.setUserInfo(loginUserRet);
        return loginResp;
    }

    private PUserToTenantEntity checkAndGetUserInfo(TUserEntity tUserEntity) {
        TenantsEntity tenantsEntity = tenantsDao.selectOne(new QueryWrapper<TenantsEntity>().eq("tenant_code", ThreadContextHolder.getTenantCode()));
        if (Objects.isNull(tenantsEntity) || tenantsEntity.getIsEnable() == 0) {
            throw MyException.exception(RetEnums.USERNAME_OR_PWD_ERROR, "租户不存在");
        }

        PUserToTenantEntity PUserToTenantEntity = pUserToTenantDao.selectOne(new QueryWrapper<PUserToTenantEntity>().eq("openid", tUserEntity.getOpenid()).eq("tenant_id", tenantsEntity.getTenantId()));
        if (Objects.isNull(PUserToTenantEntity) || PUserToTenantEntity.getEnable() == 0) {
            throw MyException.exception(RetEnums.USER_NOT_EXIST, "用户不存在");
        }
        return PUserToTenantEntity;
    }

    public LoginResp<LoginUserRet> genToken(String refreshToken, String tenantCode, String openid, String token, TUserEntity tUserEntity) {
        long timestamp = System.currentTimeMillis();
        String randStr = TokenUtils.generateRandStr();

        LoginResp<LoginUserRet> loginResp = new LoginResp<>();
        loginResp.setToken(token);
        loginResp.setRefreshToken(refreshToken);
        loginResp.setTimestamp(timestamp);
        loginResp.setRandStr(randStr);

        TokenInfo tokenInfo = new TokenInfo(tenantCode, openid, tUserEntity.getUserCode(), tUserEntity.getUserName(), randStr, timestamp);
        saveToken(token, tokenInfo);
        ThreadContextHolder.put(GlobalConstant.LOGIN_USER_INFO, tokenInfo);

        RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(token, openid, tenantCode, timestamp);
        saveRefreshToken(refreshToken, refreshTokenInfo);

        UserTokenInfo userTokenInfo = new UserTokenInfo(token, refreshToken);
        saveUserTokenInfo(openid, userTokenInfo);

        return loginResp;
    }

    public void saveToken(String token, TokenInfo tokenInfo) {
        redisTemplate.opsForValue().set(GlobalConstant.TOKEN_CACHE_PREFIX + token, tokenInfo, 7, TimeUnit.DAYS);
    }

    public TokenInfo getTokenInfo(String token) {
        return (TokenInfo) redisTemplate.opsForValue().get(GlobalConstant.TOKEN_CACHE_PREFIX + token);
    }

    public void delToken(String token) {
        redisTemplate.delete(GlobalConstant.TOKEN_CACHE_PREFIX + token);
    }

    public void saveRefreshToken(String refreshToken, RefreshTokenInfo refreshTokenInfo) {
        redisTemplate.opsForValue().set(GlobalConstant.REFRESH_TOKEN_CACHE_PREFIX + refreshToken, refreshTokenInfo, 30, TimeUnit.DAYS);
    }

    public RefreshTokenInfo getRefreshTokenInfo(String refreshToken) {
        return (RefreshTokenInfo) redisTemplate.opsForValue().get(GlobalConstant.REFRESH_TOKEN_CACHE_PREFIX + refreshToken);
    }

    public void delRefreshToken(String refreshToken) {
        redisTemplate.delete(GlobalConstant.REFRESH_TOKEN_CACHE_PREFIX + refreshToken);
    }

    public void saveUserTokenInfo(String openid, UserTokenInfo userTokenInfo) {

        UserTokenInfo oldUserTokenInfo = getUserTokenInfo(openid);
        if (!ObjectUtils.isEmpty(oldUserTokenInfo)) {
            delToken(oldUserTokenInfo.getToken());
            delRefreshToken(oldUserTokenInfo.getRefreshToken());
        }

        redisTemplate.opsForValue().set(GlobalConstant.USER_TOKEN_CACHE_PREFIX + openid, userTokenInfo, 7, TimeUnit.DAYS);
    }

    public UserTokenInfo getUserTokenInfo(String openid) {
        return (UserTokenInfo) redisTemplate.opsForValue().get(GlobalConstant.USER_TOKEN_CACHE_PREFIX + openid);
    }

    public void delUserTokenInfo(String openid) {
        redisTemplate.delete(GlobalConstant.USER_TOKEN_CACHE_PREFIX + openid);
    }


}
