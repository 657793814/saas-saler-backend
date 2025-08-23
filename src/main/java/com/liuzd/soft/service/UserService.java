package com.liuzd.soft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuzd.soft.entity.TUserEntity;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.user.AddUserReq;
import com.liuzd.soft.vo.user.EditUserReq;
import com.liuzd.soft.vo.user.UserPageReq;

/**
 * @author:liuzd01
 * @date:2023/10/31
 * @email:liuzd2025@qq.com
 * @description
 **/
public interface UserService extends IService<TUserEntity> {
    PageResult<TUserEntity> pageQuery(UserPageReq pageRequest);

    void addUser(AddUserReq addUserReq);

    void editUser(EditUserReq editUserReq);
}
