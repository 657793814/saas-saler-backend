package com.liuzd.soft.entity.es;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品搜索实体类
 *
 * @author: liuzd
 * @date: 2025/8/27
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
@NoArgsConstructor
@Document(indexName = "products")
public class ProductSearchEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private String tenantCode;

    @Field(type = FieldType.Keyword)
    private String code;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String desc;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String detail;

    @Field(type = FieldType.Keyword)
    private String imgUrls;

    @Field(type = FieldType.Integer)
    private Integer enable;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Keyword)
    private String createUser;

    @Field(type = FieldType.Date)
    private Date updateTime;

    @Field(type = FieldType.Keyword)
    private String updateUser;

    @Field(type = FieldType.Integer)
    private Integer platformStatus;

    @Field(type = FieldType.Integer)
    private Integer oneCategory;

    @Field(type = FieldType.Integer)
    private Integer twoCategory;

    @Field(type = FieldType.Integer)
    private Integer threeCategory;

    @Field(type = FieldType.Integer)
    private Integer shippingTemplateId;

}
