package com.liuzd.soft.dto.shipping;

import com.liuzd.soft.entity.PShippingTemplateAreaEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 运费模板区域配置 dto类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PShippingTemplateAreaDto {

    private Long id;

    private Long templateId;

    private String areaCodes;

    private String areaNames;

    private BigDecimal firstUnit;

    private BigDecimal firstFee;

    private BigDecimal additionalUnit;

    private BigDecimal additionalFee;

    private BigDecimal freeShippingAmount;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public PShippingTemplateAreaDto(PShippingTemplateAreaEntity PShippingTemplateAreaEntity) {
        this.id = PShippingTemplateAreaEntity.getId();
        this.templateId = PShippingTemplateAreaEntity.getTemplateId();
        this.areaCodes = PShippingTemplateAreaEntity.getAreaCodes();
        this.areaNames = PShippingTemplateAreaEntity.getAreaNames();
        this.firstUnit = PShippingTemplateAreaEntity.getFirstUnit();
        this.firstFee = PShippingTemplateAreaEntity.getFirstFee();
        this.additionalUnit = PShippingTemplateAreaEntity.getAdditionalUnit();
        this.additionalFee = PShippingTemplateAreaEntity.getAdditionalFee();
        this.freeShippingAmount = PShippingTemplateAreaEntity.getFreeShippingAmount();
        this.createdAt = PShippingTemplateAreaEntity.getCreatedAt();
        this.updatedAt = PShippingTemplateAreaEntity.getUpdatedAt();
    }
}