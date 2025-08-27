package com.liuzd.soft.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.entity.PProductsEntity;
import com.liuzd.soft.service.es.ProductSearchService;
import com.liuzd.soft.vo.product.ProductPageReq;
import com.liuzd.soft.vo.rets.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品搜索控制器
 *
 * @author: liuzd
 * @date: 2025/8/27
 * @email: liuzd2025@qq.com
 * @desc
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    /**
     * 同步所有商品到Elasticsearch
     */
    @PostMapping("/sync")
    public ResultMessage<String> syncAllProducts() {
        try {
            productSearchService.syncAllProducts();
            return ResultMessage.success("同步完成");
        } catch (Exception e) {
            log.error("同步商品到Elasticsearch失败", e);
            return ResultMessage.success("同步失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品名称搜索商品（分页）
     *
     * @return 分页结果
     */
    @PostMapping("/products/page")
    public ResultMessage<Page<PProductsEntity>> searchProductsPage(
            @RequestBody ProductPageReq req) {
        try {
            ThreadContextHolder.putTenantCode("liuzd");
            Page<PProductsEntity> pageResult = productSearchService.searchProductsPage(req);
            return ResultMessage.success(pageResult);
        } catch (Exception e) {
            log.error("分页搜索商品失败", e);
            return ResultMessage.success(new Page<>(req.getCurrent(), req.getSize(), 0));
        }
    }

    /**
     * 根据商品名称搜索商品
     *
     * @param name 商品名称
     * @return 商品列表
     */
    @GetMapping("/products")
    public ResultMessage<List<PProductsEntity>> searchProducts(@RequestParam String name) {
        try {
            List<PProductsEntity> products = productSearchService.searchProductsByName(name);
            return ResultMessage.success(products);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return ResultMessage.success(new ArrayList<>());
        }
    }
}