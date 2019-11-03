package com.atguigu.gmall191025.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

// 得到es 的结果
@Data
public class SkuLsResult implements Serializable {

    List<SkuLsInfo> skuLsInfoList;

    long total;

    long totalPages;

    List<String> attrValueIdList;

}
