package com.liuzd.soft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * p_tenants dto类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantsDto {

    private String tenantId;

    private String tenantName;

    private String tenantCode;

    private String tenantType;

    private String ownerArea;

    private String dbName;

    private String instanceId;

    private String adminName;

    private String adminMail;

    private String initPwd;

    private Date createTime;

    private Date updateTime;

    private Integer isEnable;

    private String globalCorid;

    private String adminPhone;

    private String contractNumber;

    private String from;

    private Integer status;

    private String lastErr;

    private String contractName;

    private String customerId;

    private String customerName;

    private String tocustomerId;

    private String tocustomerName;

    private String tenantSecret;

    private Integer isTop100;

    private String battlefield;

    private String ywStatus;

    private String level;

    private String domainId;

    private String khStatus;

    private String createUser;

    private String modifyUser;

    private Date modifyTime;

    private String tenantLogoUrl;

    private Integer pfId;

    private String scrmCustomerId;

    private String scrmCustomerName;

    private String scrmTocustomerId;

    private String scrmTocustomerName;

    private Date yzsExpireAt;

    private Integer regType;

    private String scrmAuthorizeGuid;


}

