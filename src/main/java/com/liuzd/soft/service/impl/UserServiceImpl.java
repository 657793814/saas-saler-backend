package com.liuzd.soft.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuzd.soft.annotation.LogAnnotation;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dao.*;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.dto.token.UserTokenInfo;
import com.liuzd.soft.entity.*;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.mq.UserMQProducerService;
import com.liuzd.soft.mq.msg.UserMsgDto;
import com.liuzd.soft.service.UserService;
import com.liuzd.soft.utils.IdUtils;
import com.liuzd.soft.utils.SecureMd5Utils;
import com.liuzd.soft.utils.TokenUtils;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.user.AddUserReq;
import com.liuzd.soft.vo.user.EditUserReq;
import com.liuzd.soft.vo.user.UserPageReq;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author: liuzd
 * @date: 2025/8/19
 * @email: liuzd2025@qq.com
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<TUserDao, TUserEntity> implements UserService {

    final TUserDao tUserDao;
    final PUserToTenantDao PUserToTenantDao;
    final PUsersDao pUsersDao;
    final TenantsDao tenantsDao;
    final TUserRoleDao tUserRoleDao;
    final UserMQProducerService userMQProducerService;
    final LoginServiceImpl loginServiceImpl;

    @Override
    public PageResult<TUserEntity> pageQuery(UserPageReq pageRequest) {
        // 创建分页对象
        Page<TUserEntity> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());

        // 构建查询条件
        QueryWrapper<TUserEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageRequest.getUserName())) {
            queryWrapper.like("uname", pageRequest.getUserName());
        }
        if (StringUtils.hasText(pageRequest.getMobile())) {
            queryWrapper.like("mobile", pageRequest.getMobile());
        }

        // 执行分页查询
        Page<TUserEntity> resultPage = tUserDao.selectPage(page, queryWrapper);

        // 封装返回结果
        PageResult<TUserEntity> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords());

        return pageResult;
    }

    @LogAnnotation
    @Override
    public void addUser(AddUserReq addUserReq) {

        String tenantCode = ThreadContextHolder.getTenantCode();
        QueryWrapper<TenantsEntity> queryWrapper = new QueryWrapper<TenantsEntity>().eq("tenant_code", tenantCode);
        TenantsEntity tenantsEntity = tenantsDao.selectOne(queryWrapper);
        Assert.notNull(tenantsEntity, () -> MyException.exception(RetEnums.TENANT_NOT_EXISTS));

        TUserEntity tUserEntity = new TUserEntity();
        String openid = IdUtils.generateOpenId();
        tUserEntity.setOpenid(openid);
        tUserEntity.setUserCode(addUserReq.getUserCode());
        tUserEntity.setUserName(addUserReq.getUserName());
        tUserEntity.setMobile(addUserReq.getMobile());
        String salt = TokenUtils.generateStr(8);
        tUserEntity.setSalt(salt);
        String pwd = SecureMd5Utils.md5WithSalt(addUserReq.getPassword(), salt);
        tUserEntity.setPassword(pwd);
        tUserEntity.setEnable(addUserReq.getEnable() ? 1 : 0);
        tUserDao.insert(tUserEntity);

        PUserToTenantEntity PUserToTenantEntity = new PUserToTenantEntity();
        String unionId = IdUtils.generateUnionId();
        PUserToTenantEntity.setUnionId(unionId);
        PUserToTenantEntity.setOpenId(openid);
        PUserToTenantEntity.setTenantId(tenantsEntity.getTenantId());
        PUserToTenantEntity.setEnable(1);
        PUserToTenantDao.insert(PUserToTenantEntity);

        PUsersEntity PUsersEntity = new PUsersEntity();
        PUsersEntity.setEnable(1);
        PUsersEntity.setUserName(addUserReq.getUserName());
        PUsersEntity.setPassword(pwd);
        PUsersEntity.setMobile(addUserReq.getMobile());
        PUsersEntity.setUnionId(unionId);
        PUsersEntity.setUserCode(addUserReq.getUserCode());
        PUsersEntity.setSalt(salt);
        pUsersDao.insert(PUsersEntity);
    }

    @LogAnnotation
    @Override
    public void editUser(EditUserReq editUserReq) {
        QueryWrapper<TUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", editUserReq.getOpenid());
        TUserEntity tUserEntity = tUserDao.selectOne(queryWrapper);
        Assert.notNull(tUserEntity, () -> MyException.exception(RetEnums.USER_NOT_EXIST));


        int changeFlag = 0;
        if (StringUtil.isNotBlank(editUserReq.getUserName())) {

            QueryWrapper<TUserEntity> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("uname", editUserReq.getUserName());
            TUserEntity tUserEntity2 = tUserDao.selectOne(queryWrapper2);
            if (tUserEntity2 != null && !editUserReq.getOpenid().equals(tUserEntity2.getOpenid())) {
                throw MyException.exception(RetEnums.USER_EXIST, "存在相同用户名");
            }

            tUserEntity.setUserName(editUserReq.getUserName());
            changeFlag = 1;
        }

        if (StringUtil.isNotBlank(editUserReq.getMobile())) {
            tUserEntity.setMobile(editUserReq.getMobile());
            changeFlag = 1;
        }

        if (Objects.nonNull(editUserReq.getEnable())) {
            tUserEntity.setEnable(editUserReq.getEnable() ? 1 : 0);
            changeFlag = 1;

            //禁用，抛出一个mq消息，异步清除用户的token
            if (!editUserReq.getEnable()) {
                UserMsgDto userMsgDto = new UserMsgDto<TUserEntity>();
                userMsgDto.setEventType(3);
                userMsgDto.setInfo(tUserEntity);
                userMQProducerService.sendObjectMessage(userMsgDto);
            }
        }

        if (StringUtil.isNotBlank(editUserReq.getPassword())) {
            tUserEntity.setPassword(SecureMd5Utils.md5WithSalt(editUserReq.getPassword(), tUserEntity.getSalt()));
            changeFlag = 1;
        }

        if (changeFlag == 0) {
            return;
        }
        tUserDao.update(tUserEntity, new UpdateWrapper<TUserEntity>().eq("openid", editUserReq.getOpenid()));
    }

    public void clearUserToken(String openid) {
        UserTokenInfo userTokenInfo = loginServiceImpl.getUserTokenInfo(openid);
        if (Objects.isNull(userTokenInfo)) {
            return;
        }
        loginServiceImpl.delToken(userTokenInfo.getToken());
        loginServiceImpl.delRefreshToken(userTokenInfo.getRefreshToken());
        loginServiceImpl.delUserTokenInfo(openid);
    }

    public boolean checkCurrentUserIsAdmin() {
        //只有管理员可以删除
        TokenInfo tokenInfo = (TokenInfo) ThreadContextHolder.get(GlobalConstant.LOGIN_USER_INFO);
        QueryWrapper queryWrapper = new QueryWrapper<TUserRoleEntity>();
        queryWrapper.eq("openid", tokenInfo.getOpenid());
        queryWrapper.eq("role_id", GlobalConstant.DEFAULT_ADMIN_ROLE_ID);
        TUserRoleEntity tUserRoleEntity = tUserRoleDao.selectOne(queryWrapper);
        return Objects.nonNull(tUserRoleEntity);
    }
}
