package com.atguigu.gmall191025.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuLsParams implements Serializable {
    // skuName
    String  keyword;
    // 三级分类Id
    String catalog3Id;
    // 平台属性值Id
    String[] valueId;
    // 默认第一页开始查询
    int pageNo=1;
    // 每页显示的条数
    int pageSize=20;

}
