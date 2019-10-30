package com.atguigu.gmall191025.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfo implements Serializable{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    String id;

    @Column
    String spuId;

    @Column
    BigDecimal price;

    @Column
    String skuName;

    @Column
    BigDecimal weight;

    @Column
    String skuDesc;

    @Column
    String catalog3Id;

    @Column
    String skuDefaultImg;

    // 商品图片
    @Transient
    List<SkuImage> skuImageList;
    // sku 与平台属性集合的实体类集合
    @Transient
    List<SkuAttrValue> skuAttrValueList;
    // sku 与销售属性集合的实体类集合
    @Transient
    List<SkuSaleAttrValue> skuSaleAttrValueList;

}
