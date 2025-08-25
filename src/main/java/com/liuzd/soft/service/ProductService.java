package com.liuzd.soft.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.product.*;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
public interface ProductService {

    PageResult<ProductPageResp> page(ProductPageReq req);

    EditProductResp detail(Integer productId) throws JsonProcessingException;

    void createProduct(CreateProductReq createProductReq) throws JsonProcessingException;

    void editProduct(CreateProductReq createProductReq) throws JsonProcessingException;

    PageResult<SpecDataResp> specData(SpecDataPageReq req);

    PageResult<SpecDataResp> specPage(SpecDataPageReq req);

    void createSpecValue(AddSpecValueReq req);

    void updateSpecTypeStatus(UpdateSpecTypeReq req);

    void updateSpecValueStatus(UpdateSpecValueReq req);

    void updateProductStatus(UpdateProductStatusReq req);

    void updateSkuStatus(UpdateSkuStatusReq req);
}