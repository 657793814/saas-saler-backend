package com.liuzd.soft.dto.shipping;

import com.liuzd.soft.entity.PShippingTemplateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 运费模板 dto类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PShippingTemplateDto {

    private Long id;

    private String templateName;

    private String tenantCode;

    private Integer valuationType;

    private BigDecimal firstFee;

    private BigDecimal additionalFee;

    private BigDecimal freeShippingAmount;

    private Integer status;

    private Integer sortOrder;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public PShippingTemplateDto(PShippingTemplateEntity PShippingTemplateEntity) {
        this.id = PShippingTemplateEntity.getId();
        this.templateName = PShippingTemplateEntity.getTemplateName();
        this.tenantCode = PShippingTemplateEntity.getTenantCode();
        this.valuationType = PShippingTemplateEntity.getValuationType();
        this.firstFee = PShippingTemplateEntity.getFirstFee();
        this.additionalFee = PShippingTemplateEntity.getAdditionalFee();
        this.freeShippingAmount = PShippingTemplateEntity.getFreeShippingAmount();
        this.status = PShippingTemplateEntity.getStatus();
        this.sortOrder = PShippingTemplateEntity.getSortOrder();
        this.createdAt = PShippingTemplateEntity.getCreatedAt();
        this.updatedAt = PShippingTemplateEntity.getUpdatedAt();
    }
}