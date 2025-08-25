package com.liuzd.soft.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.vo.page.PageResult;
import com.liuzd.soft.vo.product.*;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@RestController
@RequestMapping(path = "/products")
@RequiredArgsConstructor
public class ProductsController {

    final ProductService productService;

    @PostMapping("/page")
    public ResultMessage<PageResult<ProductPageResp>> page(@Valid @RequestBody ProductPageReq req) {
        return ResultMessage.success(productService.page(req));
    }

    @RequestMapping("/detail")
    public ResultMessage<EditProductResp> detail(@RequestParam(name = "productId", defaultValue = "0") Integer productId) throws JsonProcessingException {
        return ResultMessage.success(productService.detail(productId));
    }

    @PostMapping(path = "/create")
    public ResultMessage<Object> create(@Valid @RequestBody CreateProductReq createProductReq) throws JsonProcessingException {
        productService.createProduct(createProductReq);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/edit")
    public ResultMessage<Object> edit(@Valid @RequestBody CreateProductReq createProductReq) throws JsonProcessingException {
        productService.editProduct(createProductReq);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/updateStatus")
    public ResultMessage<Object> updateStatus(@Valid @RequestBody CreateProductReq createProductReq) throws JsonProcessingException {
        return ResultMessage.success("success");
    }


    /**
     * 商品创建页面的规格分页数据
     * 查询的时可见的
     *
     * @param req
     * @return
     */
    @PostMapping(path = "/spec_data")
    public ResultMessage<PageResult<SpecDataResp>> specData(@RequestBody SpecDataPageReq req) {
        return ResultMessage.success(productService.specData(req));
    }

    /**
     * 商品规格分页页面数据
     *
     * @param req
     * @return
     */
    @PostMapping(path = "/spec_page")
    public ResultMessage<PageResult<SpecDataResp>> specPage(@RequestBody SpecDataPageReq req) {
        return ResultMessage.success(productService.specPage(req));
    }

    @PostMapping(path = "/create_spec_value")
    public ResultMessage<Object> createSpecValue(@RequestBody AddSpecValueReq req) {
        productService.createSpecValue(req);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/update_spec_type_status")
    public ResultMessage<Object> updateSpecTypeStatus(@RequestBody UpdateSpecTypeReq req) {
        productService.updateSpecTypeStatus(req);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/update_spec_value_status")
    public ResultMessage<Object> updateSpecValueStatus(@RequestBody UpdateSpecValueReq req) {
        productService.updateSpecValueStatus(req);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/update_product_status")
    public ResultMessage<Object> updateProductStatus(@RequestBody UpdateProductStatusReq req) {
        productService.updateProductStatus(req);
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/update_sku_status")
    public ResultMessage<Object> updateSkuStatus(@RequestBody UpdateSkuStatusReq req) {
        productService.updateSkuStatus(req);
        return ResultMessage.success("success");
    }
}