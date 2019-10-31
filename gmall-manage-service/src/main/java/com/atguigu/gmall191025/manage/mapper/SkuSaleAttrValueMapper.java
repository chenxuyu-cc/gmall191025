package com.atguigu.gmall191025.manage.mapper;

import com.atguigu.gmall191025.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {
    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);

    List<Map> getSaleAttrValuesBySpu(String spuId);
}
