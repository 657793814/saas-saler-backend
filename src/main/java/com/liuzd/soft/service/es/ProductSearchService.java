package com.liuzd.soft.service.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuzd.soft.context.ThreadContextHolder;
import com.liuzd.soft.dao.PProductsDao;
import com.liuzd.soft.entity.PProductsEntity;
import com.liuzd.soft.entity.es.ProductSearchEntity;
import com.liuzd.soft.repository.ProductSearchRepository;
import com.liuzd.soft.utils.DateUtils;
import com.liuzd.soft.vo.product.ProductPageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品搜索 服务
 *
 * @author: liuzd
 * @date: 2025/8/27
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true")
public class ProductSearchService {
    private final ProductSearchRepository productSearchRepository;
    private final PProductsDao pProductsDao;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 将产品实体转换为搜索实体
     *
     * @param product 产品实体
     * @return 搜索实体
     */
    public ProductSearchEntity convertToSearchEntity(PProductsEntity product) {
        ProductSearchEntity searchEntity = new ProductSearchEntity();
        searchEntity.setId(product.getId());
        searchEntity.setTenantCode(product.getTenantCode());
        searchEntity.setCode(product.getCode());
        searchEntity.setName(product.getName());
        searchEntity.setDesc(product.getDesc());
        searchEntity.setDetail(product.getDetail());
        searchEntity.setImgUrls(product.getImgUrls());
        searchEntity.setEnable(product.getEnable());
        searchEntity.setCreateTime(DateUtils.parseDate(product.getCreateTime()));
        searchEntity.setCreateUser(product.getCreateUser());
        searchEntity.setUpdateTime(DateUtils.parseDate(product.getUpdateTime()));
        searchEntity.setUpdateUser(product.getUpdateUser());
        searchEntity.setPlatformStatus(product.getPlatformStatus());
        searchEntity.setOneCategory(product.getOneCategory());
        searchEntity.setTwoCategory(product.getTwoCategory());
        searchEntity.setThreeCategory(product.getThreeCategory());
        searchEntity.setShippingTemplateId(product.getShippingTemplateId());
        return searchEntity;
    }

    /**
     * 搜索实体转化成产品实体
     *
     * @param searchEntity
     * @return
     */
    public PProductsEntity convertToEntity(ProductSearchEntity searchEntity) {
        PProductsEntity productsEntity = new PProductsEntity();
        productsEntity.setId(searchEntity.getId());
        productsEntity.setTenantCode(searchEntity.getTenantCode());
        productsEntity.setCode(searchEntity.getCode());
        productsEntity.setName(searchEntity.getName());
        productsEntity.setDesc(searchEntity.getDesc());
        productsEntity.setDetail(searchEntity.getDetail());
        productsEntity.setImgUrls(searchEntity.getImgUrls());
        productsEntity.setEnable(searchEntity.getEnable());
        productsEntity.setCreateTime(DateUtils.formatDate(searchEntity.getCreateTime()));
        productsEntity.setCreateUser(searchEntity.getCreateUser());
        productsEntity.setUpdateTime(DateUtils.formatDate(searchEntity.getUpdateTime()));
        productsEntity.setUpdateUser(searchEntity.getUpdateUser());
        productsEntity.setPlatformStatus(searchEntity.getPlatformStatus());
        productsEntity.setOneCategory(searchEntity.getOneCategory());
        productsEntity.setTwoCategory(searchEntity.getTwoCategory());
        productsEntity.setThreeCategory(searchEntity.getThreeCategory());
        productsEntity.setShippingTemplateId(searchEntity.getShippingTemplateId());
        return productsEntity;
    }

    /**
     * 保存或更新商品到Elasticsearch
     *
     * @param product 产品实体
     */
    public void saveProductToES(PProductsEntity product) {
        try {
            ProductSearchEntity searchEntity = convertToSearchEntity(product);
            productSearchRepository.save(searchEntity);
            log.info("成功将产品同步到Elasticsearch，产品ID: {}", product.getId());
        } catch (Exception e) {
            log.warn("同步产品到Elasticsearch失败，产品ID: {}，错误信息: {}", product.getId(), e.getMessage());
        }
    }

    /**
     * 根据ID删除商品
     *
     * @param productId 产品ID
     */
    public void deleteProductFromES(Integer productId) {
        try {
            productSearchRepository.deleteById(productId);
            log.info("成功从Elasticsearch删除产品，产品ID: {}", productId);
        } catch (Exception e) {
            log.warn("从Elasticsearch删除产品失败，产品ID: {}，错误信息: {}", productId, e.getMessage());
        }
    }

    /**
     * 根据名称搜索商品（分页）
     *
     * @return 分页结果
     */
    public Page<PProductsEntity> searchProductsPage(ProductPageReq req) {
        Page<PProductsEntity> resultPage = new Page<>(req.getCurrent(), req.getSize());
        try {
            // 获取当前租户代码
            String tenantCode = ThreadContextHolder.getTenantCode();
            String name = req.getName();
            String code = req.getCode();
            Boolean enable = req.getEnable();

            // 构建bool查询
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();

            // 必须条件：tenantCode
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field("tenantCode")
                            .value(tenantCode)
                    )
            );

            // 可选条件：name
            if (StringUtils.hasText(req.getName())) {
                boolQuery.must(m -> m
                        .match(mt -> mt
                                .field("name")
                                .query(req.getName())
                        )
                );
            }

            // 可选条件：code
            if (StringUtils.hasText(req.getCode())) {
                boolQuery.must(m -> m
                        .term(t -> t
                                .field("code")
                                .value(req.getCode())
                        )
                );
            }

            // 可选条件：enable
            if (req.getEnable() != null) {
                boolQuery.filter(f -> f
                        .term(t -> t
                                .field("enable")
                                .value(req.getEnable())
                        )
                );
            }

            Query query = Query.of(q -> q.bool(boolQuery.build()));

            // 计算from值（Elasticsearch从0开始）
            int from = (req.getCurrent() - 1) * req.getSize();

            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query(query)
                    .from(from)
                    .size(req.getSize())
            );

            // 执行搜索
            SearchResponse<ProductSearchEntity> searchResponse = elasticsearchClient.search(searchRequest, ProductSearchEntity.class);

            // 处理搜索结果
            List<PProductsEntity> products = new ArrayList<>();
            if (searchResponse.hits().hits() != null) {
                for (Hit<ProductSearchEntity> hit : searchResponse.hits().hits()) {
                    if (hit.source() != null) {
                        products.add(convertToEntity(hit.source()));
                    }
                }
            }

            // 获取总记录数
            long total = searchResponse.hits().total() != null ?
                    searchResponse.hits().total().value() : 0;
            resultPage.setTotal(total);
            resultPage.setRecords(products);
            log.info("从Elasticsearch搜索到 {} 个商品，总记录数: {}", products.size(), total);
            return resultPage;
        } catch (IOException e) {
            log.warn("从Elasticsearch搜索产品失败，搜索关键词: {}，IO异常: {}", req.getName(), e.getMessage());
            return resultPage;
        } catch (Exception e) {
            log.warn("从Elasticsearch搜索产品失败，搜索关键词: {}，异常: {}", req.getName(), e.getMessage());
            return resultPage;
        }
    }

    /**
     * 根据名称搜索商品
     *
     * @param name 商品名称
     * @return 商品列表
     */
    public List<PProductsEntity> searchProductsByName(String name) {
        try {
            // 构建查询条件
            Query query = Query.of(q -> q
                    .match(m -> m
                            .field("name")
                            .query(name)
                    )
            );

            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query(query)
                    .size(100)  // 限制返回结果数量
            );

            // 执行搜索
            SearchResponse<ProductSearchEntity> searchResponse = elasticsearchClient.search(searchRequest, ProductSearchEntity.class);
            log.info("search content:{}", searchResponse);


            // 处理搜索结果
            List<PProductsEntity> products = new ArrayList<>();
            if (searchResponse.hits().hits() != null) {
                for (Hit<ProductSearchEntity> hit : searchResponse.hits().hits()) {
                    if (hit.source() != null) {
                        products.add(convertToEntity(hit.source()));
                    }
                }
            }

            log.info("从Elasticsearch搜索到 {} 个商品", products.size());
            return products;
        } catch (IOException e) {
            log.warn("从Elasticsearch搜索产品失败，搜索关键词: {}，错误信息: {}", name, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("从Elasticsearch搜索产品失败，搜索关键词: {}，错误信息: {}", name, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 批量同步所有商品到Elasticsearch
     */
    public void syncAllProducts() {
        try {
            List<PProductsEntity> products = pProductsDao.selectList(null);
            List<ProductSearchEntity> searchEntities = products.stream()
                    .map(this::convertToSearchEntity)
                    .collect(Collectors.toList());
            productSearchRepository.saveAll(searchEntities);
            log.info("成功同步所有产品到Elasticsearch，共 {} 条记录", products.size());
        } catch (Exception e) {
            log.warn("批量同步产品到Elasticsearch失败，错误信息: {}", e.getMessage());
        }
    }
}