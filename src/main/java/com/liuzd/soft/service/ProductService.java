package com.liuzd.soft.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liuzd.soft.vo.product.CreateProductReq;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
public interface ProductService {

    void createProduct(CreateProductReq createProductReq) throws JsonProcessingException;
}