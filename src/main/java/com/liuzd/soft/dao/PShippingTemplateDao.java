package com.liuzd.soft.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.entity.PShippingTemplateEntity;
import org.springframework.stereotype.Repository;

/**
 * shipping_template dao类
 */
@DS(GlobalConstant.DEFAULT_DB_KEY)
@Repository
public interface PShippingTemplateDao extends BaseMapper<PShippingTemplateEntity> {

}
