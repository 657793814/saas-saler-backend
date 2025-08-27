package com.liuzd.soft.consts;

import java.util.HashSet;
import java.util.Set;

public class ProductConstant {

    public final static Integer PRODUCT_STATUS_DISABLE = 0;
    public final static Integer PRODUCT_STATUS_ENABLE = 1;

    public final static Integer SKU_STATUS_DISABLE = 0;
    public final static Integer SKU_STATUS_ENABLE = 1;

    public final static Integer PRODUCT_PLATFORM_STATUS_AUDIT = 0;  //待审核
    public final static Integer PRODUCT_PLATFORM_STATUS_OFF = 1;  //下架
    public final static Integer PRODUCT_PLATFORM_STATUS_NORMAL = 2;  //上架

    //计费方式 1：按件 2：按重量
    public final static Integer SPEC_TYPE_DISABLE = 0;
    public final static Integer SPEC_TYPE_ENABLE = 1;

    public final static Integer VALUATION_TYPE_NUM = 1;
    public final static Integer VALUATION_TYPE_WEIGHT = 2;

    public final static Set<Integer> VALUATION_TYPE_MAP = new HashSet<Integer>() {{
        add(VALUATION_TYPE_NUM);
        add(VALUATION_TYPE_WEIGHT);
    }};

    public final static Integer SHIPPING_TEMPLATE_STATUS_DISABLE = 0;
    public final static Integer SHIPPING_TEMPLATE_STATUS_ENABLE = 1;

    public final static Set<Integer> SHIPPING_TEMPLATE_STATUS_MAP = new HashSet<Integer>() {{
        add(SHIPPING_TEMPLATE_STATUS_DISABLE);
        add(SHIPPING_TEMPLATE_STATUS_ENABLE);
    }};

    public final static Integer CATEGORY_STATUS_DISABLE = 0;
    public final static Integer CATEGORY_STATUS_ENABLE = 1;

}
