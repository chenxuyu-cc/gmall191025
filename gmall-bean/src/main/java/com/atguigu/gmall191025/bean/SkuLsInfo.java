package com.atguigu.gmall191025.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable {

    String id;

    BigDecimal price;

    String skuName;

    String catalog3Id;

    String skuDefaultImg;

    // 默认值，做热度排名
    Long hotScore=0L;
    // 平台属性值对象集合
    List<SkuLsAttrValue> skuAttrValueList;

}
