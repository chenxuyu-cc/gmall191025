package com.atguigu.gmall191025.service;

import com.atguigu.gmall191025.bean.SkuLsInfo;
import com.atguigu.gmall191025.bean.SkuLsParams;
import com.atguigu.gmall191025.bean.SkuLsResult;

public interface ListService {

    /**
     * 商品上架
     * @param skuLsInfo
     */
    void SkuLsInfo(SkuLsInfo skuLsInfo);

    /**
     * 1.把用户所查询的参数封装到对象里
     * 2.查询的结果也封装到一个对象里
     * @param skuLsParams
     * @return
     */
    SkuLsResult search(SkuLsParams skuLsParams);
}
