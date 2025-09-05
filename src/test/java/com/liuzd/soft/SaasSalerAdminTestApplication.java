package com.liuzd.soft;

import com.liuzd.soft.config.DynamicDataSource;
import com.liuzd.soft.dao.PUserToTenantDao;
import com.liuzd.soft.dao.PUsersDao;
import com.liuzd.soft.dao.TUserDao;
import com.liuzd.soft.entity.PUserToTenantEntity;
import com.liuzd.soft.entity.PUsersEntity;
import com.liuzd.soft.entity.TUserEntity;
import com.liuzd.soft.service.impl.RolesServiceImpl;
import com.liuzd.soft.utils.IdUtils;
import com.liuzd.soft.utils.TokenUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SaasSalerAdminTestApplication {

    @Autowired
    private TUserDao tUserDao;
    @Autowired
    private PUsersDao PUsersDao;
    @Autowired
    private PUserToTenantDao PUserToTenantDao;
    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    private RolesServiceImpl rolesServiceImpl;

    void contextLoads() {
        String userCode, username, mobile, openid, unionId, pwd, salt;

        PUserToTenantEntity PUserToTenantEntity = new PUserToTenantEntity();
        PUsersEntity PUsersEntity = new PUsersEntity();
        String tenantId = "lzd1234567890";
        int len = 100;
        List<TUserEntity> list = new ArrayList<>(len);
        for (int i = 1; i <= len; i++) {
            TUserEntity tUserEntity = new TUserEntity();

            userCode = "user_" + i;
            username = "uname_" + i;
            mobile = "135" + IdUtils.padNumber(i, 8);
            openid = IdUtils.generateOpenId(i);
            salt = TokenUtils.generateStr(8);

            pwd = DigestUtils.md5Hex(DigestUtils.md5Hex("123456") + salt);
            unionId = IdUtils.generateUnionId(i);

            tUserEntity.setUserCode(userCode);
            tUserEntity.setOpenid(openid);
            tUserEntity.setUserName(username);
            tUserEntity.setMobile(mobile);
            tUserEntity.setPassword(pwd);
            tUserEntity.setEnable(1);
            tUserEntity.setSalt(salt);
            list.add(tUserEntity);

            PUserToTenantEntity.setUnionId(unionId);
            PUserToTenantEntity.setOpenId(openid);
            PUserToTenantEntity.setTenantId(tenantId);
            PUserToTenantEntity.setEnable(1);
            PUserToTenantDao.insert(PUserToTenantEntity);

            PUsersEntity.setEnable(1);
            PUsersEntity.setUserName(username);
            PUsersEntity.setPassword(pwd);
            PUsersEntity.setMobile(mobile);
            PUsersEntity.setUnionId(unionId);
            PUsersEntity.setUserCode(userCode);
            PUsersEntity.setSalt(salt);
            PUsersDao.insert(PUsersEntity);
        }

        dynamicDataSource.switchTenant("liuzd");
        for (TUserEntity user : list) {
            tUserDao.insert(user);
        }
    }


    void md5() {
        System.out.println(DigestUtils.md5Hex("123456"));
    }

    @Test
    void testMenus() {
        dynamicDataSource.switchTenant("liuzd");
        rolesServiceImpl.queryUserMenus("openid_00000000000000000000003");
    }
}
