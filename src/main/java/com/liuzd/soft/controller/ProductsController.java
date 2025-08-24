package com.liuzd.soft.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.vo.product.CreateProductReq;
import com.liuzd.soft.vo.product.ProductPageReq;
import com.liuzd.soft.vo.rets.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResultMessage<Object> page(@Valid @RequestBody ProductPageReq req) {
        return ResultMessage.success(req);
    }

    @PostMapping("/detail")
    public ResultMessage<Object> detail() {
        return ResultMessage.success("success");
    }

    @PostMapping(path = "/create")
    public ResultMessage<Object> create(@Valid @RequestBody CreateProductReq createProductReq) throws JsonProcessingException {
        productService.createProduct(createProductReq);
        return ResultMessage.success("success");
    }
}