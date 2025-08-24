package com.liuzd.soft.controller;

import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.vo.product.CreateProductReq;
import com.liuzd.soft.vo.rets.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResultMessage<List<Object>> page() {
        return ResultMessage.success(null);
    }

    @PostMapping("/detail")
    public ResultMessage<Object> detail() {
        return ResultMessage.success("success");
    }

    @PostMapping("/create")
    public ResultMessage<Object> create(@RequestBody CreateProductReq createProductReq) {
        productService.createProduct(createProductReq);
        return ResultMessage.success("success");
    }

}
