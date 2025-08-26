package com.liuzd.soft.dto.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuzd.soft.entity.PProductCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 商品分类 dto类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class PProductCategoryDto {

    private Integer id;

    private String name;

    private Integer parentId;

    private Integer level;

    private Integer status;

    private Integer sortOrder;

    private String iconUrl;

    private String imageUrl;

    private String description;

    private String seoTitle;

    private String seoKeywords;

    private String seoDescription;

    private Timestamp createdAt;

    private Timestamp updatedAt;


    public PProductCategoryDto(PProductCategoryEntity pProductCategoryEntity) {
        this.id = pProductCategoryEntity.getId();
        this.name = pProductCategoryEntity.getName();
        this.parentId = pProductCategoryEntity.getParentId();
        this.level = pProductCategoryEntity.getLevel();
        this.status = pProductCategoryEntity.getStatus();
        this.sortOrder = pProductCategoryEntity.getSortOrder();
        this.iconUrl = pProductCategoryEntity.getIconUrl();
        this.imageUrl = pProductCategoryEntity.getImageUrl();
        this.description = pProductCategoryEntity.getDescription();
        this.seoTitle = pProductCategoryEntity.getSeoTitle();
        this.seoKeywords = pProductCategoryEntity.getSeoKeywords();
        this.seoDescription = pProductCategoryEntity.getSeoDescription();
        this.createdAt = pProductCategoryEntity.getCreatedAt();
        this.updatedAt = pProductCategoryEntity.getUpdatedAt();
    }
}