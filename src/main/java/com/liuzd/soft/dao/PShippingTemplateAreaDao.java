package com.liuzd.soft.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.entity.PShippingTemplateAreaEntity;
import org.springframework.stereotype.Repository;

/**
 * @author: liuzd
 * @date: 2025/8/26
 * @email: liuzd2025@qq.com
 * @desc
 */
@DS(GlobalConstant.DEFAULT_DB_KEY)
@Repository
public interface PShippingTemplateAreaDao extends BaseMapper<PShippingTemplateAreaEntity> {
}
