package com.liuzd.soft.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liuzd.soft.dto.product.PProductCategoryDto;
import com.liuzd.soft.dto.shipping.PShippingTemplateDto;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.product.*;

import java.util.List;

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

    PageResult<PShippingTemplateDto> shippingTemplatePage(ShippingPageReq req);

    List<PShippingTemplateDto> getShippingTemplateData();

    void createShippingTemplate(CreateShippingTemplateReq req);

    void editShippingTemplate(CreateShippingTemplateReq req);

    void delShippingTemplate(Integer id);

    List<PProductCategoryDto> getProductCategoryData();
}