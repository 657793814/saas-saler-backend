package com.liuzd.soft.repository;

import com.liuzd.soft.entity.es.ProductSearchEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: liuzd
 * @date: 2025/8/27
 * @email: liuzd2025@qq.com
 * @desc
 */
@Repository
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true")
public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchEntity, Integer> {
    List<ProductSearchEntity> findByNameContaining(String name);

    List<ProductSearchEntity> findByTenantCode(String tenantCode);

    List<ProductSearchEntity> findByEnable(Integer enable);

}
