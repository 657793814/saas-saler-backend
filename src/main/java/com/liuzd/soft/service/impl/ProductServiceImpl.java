package com.liuzd.soft.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dao.ItemsDao;
import com.liuzd.soft.dao.ProductsDao;
import com.liuzd.soft.dao.TSpecTypeDao;
import com.liuzd.soft.dao.TSpecValueDao;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.entity.PItemsEntity;
import com.liuzd.soft.entity.PProductsEntity;
import com.liuzd.soft.entity.TSpecTypeEntity;
import com.liuzd.soft.entity.TSpecValueEntity;
import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.utils.DateUtils;
import com.liuzd.soft.utils.IdUtils;
import com.liuzd.soft.vo.product.CreateProductReq;
import com.liuzd.soft.vo.product.SkuInfo;
import com.liuzd.soft.vo.product.SpecInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    final ObjectMapper objectMapper;
    final ProductsDao productsDao;
    final ItemsDao itemsDao;
    final TSpecTypeDao tSpecTypeDao;
    final TSpecValueDao tSpecValueDao;


    @Override
    public void createProduct(CreateProductReq createProductReq) throws JsonProcessingException {
        String tenantCode = ThreadContextHolder.getTenantCode();
        TokenInfo tokenInfo = (TokenInfo) ThreadContextHolder.get(GlobalConstant.LOGIN_USER_INFO);
        PProductsEntity product = new PProductsEntity();
        product.setTenantCode(tenantCode);
        product.setCode(IdUtils.generateProductCode());
        product.setName(createProductReq.getName());
        product.setDetail(createProductReq.getDetail());
        product.setDesc(createProductReq.getDesc());
        product.setImgUrls(objectMapper.writeValueAsString(createProductReq.getImageUrls()));
        product.setEnable(0); //默认不上架
        product.setCreateUser(tokenInfo.getUserCode());
        product.setUpdateUser(tokenInfo.getUserCode());
        product.setCreateTime(DateUtils.getCurrentDateTimeString());
        product.setUpdateTime(DateUtils.getCurrentDateTimeString());
        productsDao.insert(product);
        int productId = product.getId();
        log.info("insert product ,productInfo:{}", productId, product);

        for (SkuInfo skuInfo : createProductReq.getSkus()) {
            PItemsEntity item = new PItemsEntity();
            item.setProductId(productId);
            item.setImg("");
            item.setStock(skuInfo.getStock());
            item.setSalePrice(skuInfo.getSalePrice());
            item.setCostPrice(skuInfo.getCostPrice());

            //处理规格数据,单规格没有这个数据
            List<SpecInfo> specList = skuInfo.getSpecList();
            if (CollUtil.isNotEmpty(specList)) {
                for (SpecInfo specInfo : specList) {
                    int specTypeId, specValueId;
                    if (specInfo.getSpecTypeId().contains(GlobalConstant.SPEC_TYPE_PREFIX)) {
                        TSpecTypeEntity specTypeEntity;
                        //先查询，如果没有再创建
                        QueryWrapper<TSpecTypeEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("name", specInfo.getSpecTypeName());
                        specTypeEntity = tSpecTypeDao.selectOne(queryWrapper);
                        if (Objects.isNull(specTypeEntity)) {
                            specTypeEntity = new TSpecTypeEntity();
                            specTypeEntity.setName(specInfo.getSpecTypeName());
                            tSpecTypeDao.insert(specTypeEntity);
                        }
                        specTypeId = specTypeEntity.getId();
                        specInfo.setSpecTypeId(specTypeId + "");
                    } else {
                        specTypeId = Integer.parseInt(specInfo.getSpecTypeId());
                    }

                    if (specInfo.getSpecValueId().contains(GlobalConstant.SPEC_VALUE_PREFIX)) {
                        TSpecValueEntity specValueEntity;
                        QueryWrapper<TSpecValueEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("spec_type_id", specTypeId);
                        queryWrapper.eq("value", specInfo.getSpecValueName());
                        specValueEntity = tSpecValueDao.selectOne(queryWrapper);
                        if (Objects.isNull(specValueEntity)) {
                            specValueEntity = new TSpecValueEntity();
                            specValueEntity.setSpecTypeId(specTypeId);
                            specValueEntity.setValue(specInfo.getSpecValueName());
                            tSpecValueDao.insert(specValueEntity);
                        }
                        specValueId = specValueEntity.getId();
                        specInfo.setSpecValueId(specValueId + "");
                    }
                }
                item.setSpecData(objectMapper.writeValueAsString(specList));
            } else {
                item.setSpecData("");
            }

            itemsDao.insert(item);
            log.info("insert sku ,skuInfo:{}", skuInfo);
        }

    }
}