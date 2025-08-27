package com.liuzd.soft.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch配置类
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.liuzd.soft.repository",
        considerNestedRepositories = true)
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true")
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;


    /**
     * 处理es序列化问题
     * 否则需要在es实体中增加字段 _class
     *
     * @return
     */
    @Bean("elasticsearchObjectMapper")
    public ObjectMapper elasticsearchObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public RestClient getRestClient() {

        RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticsearchUri));

        // 配置请求超时时间
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(5000); // 连接超时5秒
            requestConfigBuilder.setSocketTimeout(60000); // 套接字超时60秒
            requestConfigBuilder.setConnectionRequestTimeout(10000); // 连接请求超时10秒
            return requestConfigBuilder;
        });

        // 配置HttpClient超时
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(100);
            httpClientBuilder.setMaxConnPerRoute(20);
            return httpClientBuilder;
        });

        return builder.build();
    }

    @Bean
    public ElasticsearchTransport getElasticsearchTransport(ObjectMapper elasticsearchObjectMapper) {
        RestClient restClient = getRestClient();
        // 使用自定义的ObjectMapper
        JacksonJsonpMapper mapper = new JacksonJsonpMapper(elasticsearchObjectMapper);
        return new RestClientTransport(restClient, mapper);
    }

    @Bean
    public ElasticsearchClient getElasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}