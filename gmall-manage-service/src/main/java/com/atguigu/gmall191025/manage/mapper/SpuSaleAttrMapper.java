package com.atguigu.gmall191025.manage.mapper;

import com.atguigu.gmall191025.bean.SkuInfo;
import com.atguigu.gmall191025.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);
}
