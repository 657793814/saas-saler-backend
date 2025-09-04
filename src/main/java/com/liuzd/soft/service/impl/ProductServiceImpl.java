package com.liuzd.soft.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuzd.soft.config.ProjectProperties;
import com.liuzd.soft.consts.GlobalConstant;
import com.liuzd.soft.consts.ProductConstant;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dao.*;
import com.liuzd.soft.dto.product.PProductCategoryDto;
import com.liuzd.soft.dto.shipping.PShippingTemplateDto;
import com.liuzd.soft.dto.token.TokenInfo;
import com.liuzd.soft.entity.*;
import com.liuzd.soft.enums.RetEnums;
import com.liuzd.soft.exception.MyException;
import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.service.es.ProductSearchService;
import com.liuzd.soft.utils.DateUtils;
import com.liuzd.soft.utils.IdUtils;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.product.*;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * saas 商家后台产品逻辑
 *
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
    final PProductsDao pProductsDao;
    final PItemsDao pItemsDao;
    final TSpecTypeDao tSpecTypeDao;
    final TSpecValueDao tSpecValueDao;
    final MinioService minioService;
    final PShippingTemplateDao pShippingTemplateDao;
    final PProductCategoryDao pProductCategoryDao;
    final Optional<ProductSearchService> productSearchService;
    final ProjectProperties projectProperties;


    @Override
    public void createProduct(CreateProductReq createProductReq) throws JsonProcessingException {
        String tenantCode = ThreadContextHolder.getTenantCode();
        TokenInfo tokenInfo = (TokenInfo) ThreadContextHolder.get(GlobalConstant.LOGIN_USER_INFO);
        PProductsEntity product = new PProductsEntity();
        fillProductInfo(createProductReq, product, tenantCode, tokenInfo);
        product.setEnable(ProductConstant.PRODUCT_STATUS_ENABLE);
        product.setCreateUser(tokenInfo.getUserCode());
        product.setCreateTime(DateUtils.getCurrentDateTimeString());
        pProductsDao.insert(product);
        int productId = product.getId();
        log.info("insert product ,productInfo:{}", product);

        // 同步到Elasticsearch
        if (projectProperties.isEsEnabled()) {
            productSearchService.get().saveProductToES(product);
        }

        handleProductSku(createProductReq, productId, false);
    }

    @Override
    public void editProduct(CreateProductReq editProductReq) throws JsonProcessingException {
        Assert.notNull(editProductReq.getId(), () -> MyException.exception(RetEnums.PRODUCT_EDIT_ERROR));
        QueryWrapper<PProductsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", editProductReq.getId());
        PProductsEntity product = pProductsDao.selectOne(queryWrapper);
        Assert.notNull(product, () -> MyException.exception(RetEnums.PRODUCT_NOT_EXIST));

        String tenantCode = ThreadContextHolder.getTenantCode();
        TokenInfo tokenInfo = (TokenInfo) ThreadContextHolder.get(GlobalConstant.LOGIN_USER_INFO);

        fillProductInfo(editProductReq, product, tenantCode, tokenInfo);
        pProductsDao.update(product, queryWrapper);
        log.info("update product ,productInfo:{}", product);

        // 同步到Elasticsearch
        if (projectProperties.isEsEnabled()) {
            productSearchService.get().saveProductToES(product);
        }
        int productId = product.getId();
        handleProductSku(editProductReq, productId, true);

    }

    private void handleProductSku(CreateProductReq createProductReq, int productId, boolean isEdit) throws JsonProcessingException {
        for (SkuInfo skuInfo : createProductReq.getSkus()) {

            PItemsEntity item = new PItemsEntity();
            boolean existSku = false;
            //查询
            QueryWrapper<PItemsEntity> itemQueryWrapper = new QueryWrapper<>();
            itemQueryWrapper.eq("id", skuInfo.getSkuId());
            if (isEdit && skuInfo.getSkuId() > 0) {
                item = pItemsDao.selectOne(itemQueryWrapper);
                //健壮性处理
                if (Objects.nonNull(item)) {
                    if (item.getProductId() != productId) {  //正常 productId 是匹配的，不匹配就是异常提交
                        existSku = false;
                    } else {
                        existSku = true;
                    }
                } else {
                    existSku = false;
                }
            }

            if (!existSku) {
                item = new PItemsEntity();
                item.setProductId(productId);
                item.setCode(IdUtils.generateSkuCode());
                item.setEnable(ProductConstant.SKU_STATUS_ENABLE);  //默认启用
            }

            item.setImg(skuInfo.getImage());
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

            if (existSku) {
                pItemsDao.update(item, itemQueryWrapper);
                log.info("update sku ,skuInfo:{}", item);
            } else {
                pItemsDao.insert(item);
                log.info("insert sku ,skuInfo:{}", item);
            }

        }
    }

    private void fillProductInfo(CreateProductReq createProductReq, PProductsEntity product, String tenantCode, TokenInfo tokenInfo) throws JsonProcessingException {
        product.setTenantCode(tenantCode);
        product.setCode(IdUtils.generateProductCode());
        product.setName(createProductReq.getName());
        product.setDetail(createProductReq.getDetail());
        product.setDesc(createProductReq.getDesc());
        product.setImgUrls(objectMapper.writeValueAsString(createProductReq.getImageUrls()));
        product.setUpdateUser(tokenInfo.getUserCode());
        product.setUpdateTime(DateUtils.getCurrentDateTimeString());

        //商家更新会变成审核状态
        product.setPlatformStatus(ProductConstant.PRODUCT_PLATFORM_STATUS_AUDIT);
        product.setShippingTemplateId(createProductReq.getShippingTemplateId());
        product.setThreeCategory(createProductReq.getThreeCategoryId());
        Map<Integer, Integer> map = categoryMap();
        Integer twoCate = map.get(createProductReq.getThreeCategoryId());
        product.setTwoCategory(twoCate);
        Integer oneCate = map.get(twoCate);
        product.setOneCategory(oneCate);

    }

    private Map<Integer, Integer> categoryMap() {
        List<PProductCategoryEntity> categoryList = pProductCategoryDao.selectList(new QueryWrapper<>());
        return categoryList.stream().collect(Collectors.toMap(PProductCategoryEntity::getId, PProductCategoryEntity::getParentId));
    }


    @Override
    public PageResult<SpecDataResp> specData(SpecDataPageReq req) {
        return getSpecDataRespPageResult(req, false);
    }

    @Override
    public PageResult<SpecDataResp> specPage(SpecDataPageReq req) {
        return getSpecDataRespPageResult(req, true);
    }

    @NotNull
    private PageResult<SpecDataResp> getSpecDataRespPageResult(SpecDataPageReq req, boolean all) {
        PageResult<SpecDataResp> pageResult = new PageResult<>();
        Page<TSpecTypeEntity> page = new Page<>(req.getCurrent(), req.getSize());
        QueryWrapper<TSpecTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(req.getName()), "name", req.getName());
        if (!all) {
            queryWrapper.eq("enable", 1);
        }
        queryWrapper.orderByDesc("id");
        Page<TSpecTypeEntity> resultPage = tSpecTypeDao.selectPage(page, queryWrapper);
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords().stream().map(item -> {
            SpecDataResp specDataResp = new SpecDataResp();
            specDataResp.setSpecTypeId(item.getId());
            specDataResp.setSpecTypeName(item.getName());
            specDataResp.setEnable(item.getEnable());

            QueryWrapper<TSpecValueEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("spec_type_id", item.getId());
            if (!all) {
                queryWrapper1.eq("enable", 1);
            }
            List<TSpecValueEntity> specValueList = tSpecValueDao.selectList(queryWrapper1);
            specDataResp.setSpecValues(specValueList.stream().map(item1 -> {
                SpecValue specValue = new SpecValue();
                specValue.setSpecId(item1.getId());
                specValue.setSpecValue(item1.getValue());
                specValue.setEnable(item1.getEnable());
                return specValue;
            }).collect(Collectors.toList()));
            return specDataResp;
        }).collect(Collectors.toList()));
        return pageResult;
    }

    @Override
    public PageResult<ProductPageResp> page(ProductPageReq req) {
        PageResult<ProductPageResp> pageResult = new PageResult<>();


        Page<PProductsEntity> resultPage = new Page<>(req.getCurrent(), req.getSize());
        if (projectProperties.isEsEnabled()) {
            resultPage = productSearchService.get().searchProductsPage(req);
        } else {
            QueryWrapper<PProductsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
            queryWrapper.like(StringUtils.isNotBlank(req.getName()), "name", req.getName());
            queryWrapper.like(StringUtils.isNotBlank(req.getCode()), "code", req.getCode());
            queryWrapper.eq(req.getEnable() != null, "enable", req.getEnable());
            queryWrapper.orderByDesc("id");
            resultPage = pProductsDao.selectPage(resultPage, queryWrapper);
        }

        //查询sku
        List<Integer> productIds = resultPage.getRecords().stream().map(PProductsEntity::getId).collect(Collectors.toList());
        QueryWrapper<PItemsEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("product_id", productIds);
        List<PItemsEntity> itemsList = pItemsDao.selectList(queryWrapper1);
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
                    PageSkuResp pageSkuResp = new PageSkuResp();
                    pageSkuResp.setId(sku.getId());
                    pageSkuResp.setProductId(sku.getProductId());
                    pageSkuResp.setName(product.getName());
                    pageSkuResp.setCode(sku.getCode());
                    pageSkuResp.setSalePrice(sku.getSalePrice());
                    pageSkuResp.setCostPrice(sku.getCostPrice());
                    String imgUrl = StringUtils.isNotBlank(sku.getImg()) ? minioService.getFileUrl(sku.getImg()) : "";
                    pageSkuResp.setImgUrl(imgUrl);
                    pageSkuResp.setStock(sku.getStock());
                    pageSkuResp.setEnable(sku.getEnable());
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
                    pageSkuResp.setSpec(spec);
                    return pageSkuResp;
                }).collect(Collectors.toList()));
            }

            return productPageResp;
        }).collect(Collectors.toList()));

        return pageResult;
    }

    @Override
    public EditProductResp detail(Integer productId) throws JsonProcessingException {
        QueryWrapper<PProductsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId);
        queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
        PProductsEntity product = pProductsDao.selectOne(queryWrapper);
        Assert.notNull(product, () -> MyException.exception(RetEnums.PRODUCT_NOT_EXIST));

        EditProductResp editProductResp = new EditProductResp();
        editProductResp.setId(product.getId());
        editProductResp.setName(product.getName());
        editProductResp.setDesc(product.getDesc());
        editProductResp.setDetail(product.getDetail());
        editProductResp.setShippingTemplateId(product.getShippingTemplateId());
        editProductResp.setCategoryIds(new ArrayList<Integer>() {
            {
                add(product.getOneCategory());
                add(product.getTwoCategory());
                add(product.getThreeCategory());
            }
        });

        //img 处理
        List<String> imageUrls = new ArrayList<>();
        List<Map<String, String>> imgs = new ArrayList<>();
        if (StringUtils.isNotBlank(product.getImgUrls())) {
            imageUrls = objectMapper.readValue(product.getImgUrls(), new TypeReference<List<String>>() {
            });
            imgs = imageUrls.stream().map(path -> {
                Map<String, String> map = new HashMap<>();
                map.put("path", path);
                map.put("url", minioService.getFileUrl(path));
                return map;
            }).collect(Collectors.toList());

        }
        editProductResp.setImageUrls(imgs);

        //sku处理
        List<DetailSkuResp> skus = pItemsDao.selectList(new QueryWrapper<PItemsEntity>().eq("product_id", productId)).stream().map(sku -> {
            DetailSkuResp skuInfo = new DetailSkuResp();
            skuInfo.setSkuId(sku.getId());
            skuInfo.setStock(sku.getStock());
            skuInfo.setCostPrice(sku.getCostPrice());
            skuInfo.setSalePrice(sku.getSalePrice());
            Map<String, String> image = new HashMap<String, String>() {
                {
                    put("path", "");
                    put("url", "");
                }
            };
            if (StringUtils.isNotBlank(sku.getImg())) {
                image.put("path", sku.getImg());
                image.put("url", minioService.getFileUrl(sku.getImg()));
            }
            skuInfo.setImageObj(image);
            //spec 处理
            if (StringUtils.isNotBlank(sku.getSpecData())) {
                try {
                    skuInfo.setSpecList(objectMapper.readValue(sku.getSpecData(), new TypeReference<List<SpecInfo>>() {
                    }));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return skuInfo;
        }).collect(Collectors.toList());
        editProductResp.setSkus(skus);

        return editProductResp;
    }

    @Override
    public void createSpecValue(AddSpecValueReq req) {
        TSpecValueEntity specValueEntity = new TSpecValueEntity();
        specValueEntity.setSpecTypeId(req.getSpecTypeId());
        specValueEntity.setValue(req.getSpecValue());
        specValueEntity.setEnable(1);
        tSpecValueDao.insert(specValueEntity);
    }

    @Override
    public void batchAddSpec(BatchSaveSpecReq req) {
        Assert.notNull(req.getSpecTypeName(), () -> MyException.exception(RetEnums.SPEC_TYPE_NOT_EXIST, "规格类型名必传"));
        QueryWrapper<TSpecTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", req.getSpecTypeName());
        TSpecTypeEntity specTypeEntity = tSpecTypeDao.selectOne(queryWrapper);
        if (Objects.isNull(specTypeEntity)) {
            specTypeEntity = new TSpecTypeEntity();
            specTypeEntity.setName(req.getSpecTypeName());
            specTypeEntity.setEnable(ProductConstant.SPEC_TYPE_ENABLE);
            tSpecTypeDao.insert(specTypeEntity);
        }
        batchCreateSpecValue(req, specTypeEntity.getId());
    }

    @Override
    public void batchAddSpecValue(BatchSaveSpecReq req) {
        Assert.notNull(req.getSpecTypeId(), () -> MyException.exception(RetEnums.SPEC_TYPE_NOT_EXIST, "规格类型id必传"));
        Integer specTypeId = req.getSpecTypeId();
        batchCreateSpecValue(req, specTypeId);
    }

    private void batchCreateSpecValue(BatchSaveSpecReq req, Integer specTypeId) {
        req.getSpecValues().forEach(item -> {
            String value = item.get("specValue");
            Assert.notBlank(value, () -> MyException.exception(RetEnums.SPEC_VALUE_NOT_EXIST, "规格值必传"));

            // 判断规格值是否存在
            QueryWrapper<TSpecValueEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("spec_type_id", specTypeId);
            queryWrapper.eq("value", value);
            if (Objects.isNull(tSpecValueDao.selectOne(queryWrapper))) {
                TSpecValueEntity specValueEntity = new TSpecValueEntity();
                specValueEntity.setSpecTypeId(specTypeId);
                specValueEntity.setValue(value);
                specValueEntity.setEnable(1);
                tSpecValueDao.insert(specValueEntity);
            }
        });
    }

    @Override
    public void updateSpecTypeStatus(UpdateSpecTypeReq req) {
        QueryWrapper<TSpecTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", req.getSpecTypeId());
        TSpecTypeEntity specTypeEntity = tSpecTypeDao.selectOne(queryWrapper);
        Assert.notNull(specTypeEntity, () -> MyException.exception(RetEnums.SPEC_TYPE_NOT_EXIST));
        specTypeEntity.setEnable(req.getEnable());
        tSpecTypeDao.updateById(specTypeEntity);
    }

    @Override
    public void updateSpecValueStatus(UpdateSpecValueReq req) {
        QueryWrapper<TSpecValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", req.getSpecValueId());
        TSpecValueEntity specValueEntity = tSpecValueDao.selectOne(queryWrapper);
        Assert.notNull(specValueEntity, () -> MyException.exception(RetEnums.SPEC_VALUE_NOT_EXIST));
        specValueEntity.setEnable(req.getEnable());
        tSpecValueDao.updateById(specValueEntity);
    }

    @Override
    public void updateProductStatus(UpdateProductStatusReq req) {
        QueryWrapper<PProductsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", req.getProductId());
        PProductsEntity product = pProductsDao.selectOne(queryWrapper);
        Assert.notNull(product, () -> MyException.exception(RetEnums.PRODUCT_NOT_EXIST));
        product.setEnable(req.getEnable());
        pProductsDao.updateById(product);

        // 同步到Elasticsearch
        if (projectProperties.isEsEnabled()) {
            productSearchService.get().saveProductToES(product);
        }

        //批量更新商品sku状态,下架可以批量，上架不可以
        if (req.getEnable() == 0) {
            UpdateWrapper<PItemsEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("product_id", req.getProductId());
            updateWrapper.set("enable", req.getEnable());
            pItemsDao.update(null, updateWrapper);
        }


    }

    @Override
    public void updateSkuStatus(UpdateSkuStatusReq req) {
        QueryWrapper<PItemsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", req.getSkuId());
        PItemsEntity sku = pItemsDao.selectOne(queryWrapper);
        Assert.notNull(sku, () -> MyException.exception(RetEnums.SKU_NOT_EXIST));
        sku.setEnable(req.getEnable());
        pItemsDao.updateById(sku);

        //如果所有的sku都下架了，则商品也下架
        if (req.getEnable() == ProductConstant.SKU_STATUS_DISABLE) {
            UpdateWrapper<PProductsEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", sku.getProductId());
            updateWrapper.set("enable", ProductConstant.PRODUCT_STATUS_DISABLE);
            pProductsDao.update(null, updateWrapper);

            // 同步到Elasticsearch
            if (projectProperties.isEsEnabled()) {
                PProductsEntity product = pProductsDao.selectById(sku.getProductId());
                productSearchService.get().saveProductToES(product);
            }

        }
    }

    @Override
    public PageResult<PShippingTemplateDto> shippingTemplatePage(ShippingPageReq req) {
        Page<PShippingTemplateEntity> page = new Page<>(req.getCurrent(), req.getSize());
        page.setCurrent(req.getCurrent());
        page.setSize(req.getSize());
        QueryWrapper<PShippingTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
        queryWrapper.like(StringUtil.isNotBlank(req.getName()), "template_name", req.getName());

        Page<PShippingTemplateEntity> resultPage = pShippingTemplateDao.selectPage(page, queryWrapper);
        PageResult<PShippingTemplateDto> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setCurrent((int) resultPage.getCurrent());
        pageResult.setSize((int) resultPage.getSize());
        pageResult.setRecords(resultPage.getRecords().stream().map(PShippingTemplateDto::new).collect(Collectors.toList()));
        return pageResult;
    }

    @Override
    public List<PShippingTemplateDto> getShippingTemplateData() {
        QueryWrapper<PShippingTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", ProductConstant.SHIPPING_TEMPLATE_STATUS_ENABLE);
        queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
        List<PShippingTemplateEntity> list = pShippingTemplateDao.selectList(queryWrapper);
        return list.stream().map(PShippingTemplateDto::new).collect(Collectors.toList());
    }

    @Override
    public void createShippingTemplate(CreateShippingTemplateReq req) {
        PShippingTemplateEntity shippingTemplateEntity = new PShippingTemplateEntity();
        fillShippingTemplateData(shippingTemplateEntity, req);
        pShippingTemplateDao.insert(shippingTemplateEntity);

    }

    @Override
    public void editShippingTemplate(CreateShippingTemplateReq req) {
        QueryWrapper<PShippingTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", req.getId());
        queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
        PShippingTemplateEntity shippingTemplateEntity = pShippingTemplateDao.selectOne(queryWrapper);
        Assert.notNull(shippingTemplateEntity, () -> MyException.exception(RetEnums.SHIPPING_TEMPLATE_NOT_EXIST));
        fillShippingTemplateData(shippingTemplateEntity, req);
        pShippingTemplateDao.updateById(shippingTemplateEntity);

    }

    private void fillShippingTemplateData(PShippingTemplateEntity shippingTemplateEntity, CreateShippingTemplateReq req) {

        if (!ProductConstant.VALUATION_TYPE_MAP.contains(req.getValuationType())) {
            MyException.exception(RetEnums.VALUATION_TYPE_ERROR);
        }

        if (!ProductConstant.SHIPPING_TEMPLATE_STATUS_MAP.contains(req.getStatus())) {
            MyException.exception(RetEnums.SHIPPING_TEMPLATE_STATUS_ERROR);
        }

        shippingTemplateEntity.setTemplateName(req.getTemplateName());
        shippingTemplateEntity.setTenantCode(ThreadContextHolder.getTenantCode());
        shippingTemplateEntity.setValuationType(req.getValuationType());
        shippingTemplateEntity.setFirstFee(req.getFirstFee());
        shippingTemplateEntity.setAdditionalFee(req.getAdditionalFee());
        shippingTemplateEntity.setFreeShippingAmount(req.getFreeShippingAmount());
        shippingTemplateEntity.setStatus(req.getStatus());
    }

    @Override
    public void delShippingTemplate(Integer id) {
        QueryWrapper<PShippingTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("tenant_code", ThreadContextHolder.getTenantCode());
        PShippingTemplateEntity shippingTemplateEntity = pShippingTemplateDao.selectOne(queryWrapper);
        Assert.notNull(shippingTemplateEntity, () -> MyException.exception(RetEnums.SHIPPING_TEMPLATE_NOT_EXIST));

        shippingTemplateEntity.setStatus(ProductConstant.SHIPPING_TEMPLATE_STATUS_DISABLE);
        pShippingTemplateDao.updateById(shippingTemplateEntity);
    }

    @Override
    public List<PProductCategoryDto> getProductCategoryData() {
        QueryWrapper<PProductCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", ProductConstant.CATEGORY_STATUS_ENABLE);
        List<PProductCategoryEntity> productCategoryEntities = pProductCategoryDao.selectList(queryWrapper);
        return productCategoryEntities.stream().map(PProductCategoryDto::new).collect(Collectors.toList());
    }
}