package com.liuzd.soft.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    final MinioService minioService;


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
            item.setCode(IdUtils.generateSkuCode());
            item.setImg(skuInfo.getImage());
            item.setStock(skuInfo.getStock());
            item.setSalePrice(skuInfo.getSalePrice());
            item.setCostPrice(skuInfo.getCostPrice());
            item.setEnable(0);

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

    @Override
    public PageResult<SpecDataResp> specData(SpecDataPageReq req) {
        PageResult<SpecDataResp> pageResult = new PageResult<>();
        Page<TSpecTypeEntity> page = new Page<>(req.getCurrent(), req.getSize());
        QueryWrapper<TSpecTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(req.getName()), "name", req.getName());
        Page<TSpecTypeEntity> resultPage = tSpecTypeDao.selectPage(page, queryWrapper);
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords().stream().map(item -> {
            SpecDataResp specDataResp = new SpecDataResp();
            specDataResp.setSpecTypeId(item.getId());
            specDataResp.setSpecTypeName(item.getName());

            QueryWrapper<TSpecValueEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("spec_type_id", item.getId());
            List<TSpecValueEntity> specValueList = tSpecValueDao.selectList(queryWrapper1);
            specDataResp.setSpecValues(specValueList.stream().map(item1 -> {
                SpecValue specValue = new SpecValue();
                specValue.setSpecId(item1.getId());
                specValue.setSpecValue(item1.getValue());
                return specValue;
            }).collect(Collectors.toList()));
            return specDataResp;
        }).collect(Collectors.toList()));
        return pageResult;
    }

    @Override
    public PageResult<ProductPageResp> page(ProductPageReq req) {
        PageResult<ProductPageResp> pageResult = new PageResult<>();
        Page<PProductsEntity> page = new Page<>(req.getCurrent(), req.getSize());
        QueryWrapper<PProductsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(req.getName()), "name", req.getName());
        queryWrapper.like(StringUtils.isNotBlank(req.getCode()), "code", req.getCode());
        queryWrapper.eq(req.getEnable() != null, "enable", req.getEnable());
        Page<PProductsEntity> resultPage = productsDao.selectPage(page, queryWrapper);

        //查询sku
        List<Integer> productIds = resultPage.getRecords().stream().map(PProductsEntity::getId).collect(Collectors.toList());
        QueryWrapper<PItemsEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("product_id", productIds);
        List<PItemsEntity> itemsList = itemsDao.selectList(queryWrapper1);
        Map<Integer, List<PItemsEntity>> itemsMap = itemsList.stream().collect(Collectors.groupingBy(PItemsEntity::getProductId));

        //处理返回
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords().stream().map(product -> {
            ProductPageResp productPageResp = new ProductPageResp();

            productPageResp.setProductId(product.getId());
            productPageResp.setCode(product.getCode());
            productPageResp.setName(product.getName());
            productPageResp.setEnable(product.getEnable());
            String productImgUrl = "";
            if (StringUtils.isNotBlank(product.getImgUrls())) {
                try {
                    List<String> imgUrlList = objectMapper.readValue(product.getImgUrls(), new TypeReference<List<String>>() {
                    });
                    productImgUrl = imgUrlList.get(0);
                    productImgUrl = StringUtils.isNotBlank(productImgUrl) ? minioService.getFileUrl(productImgUrl) : "";
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            productPageResp.setImgUrl(productImgUrl);

            //处理sku
            if (itemsMap.containsKey(product.getId())) {
                List<PItemsEntity> skus = itemsMap.get(product.getId());
                productPageResp.setSalePrice(skus.get(0).getSalePrice());
                productPageResp.setCostPrice(skus.get(0).getCostPrice());

                productPageResp.setChildren(skus.stream().map(sku -> {
                    SkuResp skuResp = new SkuResp();
                    skuResp.setId(sku.getId());
                    skuResp.setProductId(sku.getProductId());
                    skuResp.setName(product.getName());
                    skuResp.setCode(sku.getCode());
                    skuResp.setSalePrice(sku.getSalePrice());
                    skuResp.setCostPrice(sku.getCostPrice());
                    String imgUrl = StringUtils.isNotBlank(sku.getImg()) ? minioService.getFileUrl(sku.getImg()) : "";
                    skuResp.setImgUrl(imgUrl);
                    skuResp.setStock(sku.getStock());
                    skuResp.setEnable(sku.getEnable());
                    String spec = "";
                    if (StringUtils.isNotBlank(sku.getSpecData())) {
                        try {
                            List<SpecInfo> specList = objectMapper.readValue(sku.getSpecData(), new TypeReference<List<SpecInfo>>() {
                            });
                            spec = specList.stream().map(item -> item.getSpecTypeName() + ":" + item.getSpecValueName()).collect(Collectors.joining(","));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    skuResp.setSpec(spec);
                    return skuResp;
                }).collect(Collectors.toList()));
            }

            return productPageResp;
        }).collect(Collectors.toList()));

        return pageResult;
    }
}