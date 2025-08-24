package com.liuzd.soft.service.impl;

import com.liuzd.soft.annotation.LogAnnotation;
import com.liuzd.soft.dao.ItemsDao;
import com.liuzd.soft.dao.ProductsDao;
import com.liuzd.soft.service.ProductService;
import com.liuzd.soft.vo.product.CreateProductReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: liuzd
 * @date: 2025/8/24
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    final ProductsDao productsDao;
    final ItemsDao itemsDao;

    public ProductServiceImpl(ProductsDao productsDao, ItemsDao itemsDao) {
        this.productsDao = productsDao;
        this.itemsDao = itemsDao;
    }

    @LogAnnotation
    @Override
    public void createProduct(CreateProductReq createProductReq) {

    }
}
