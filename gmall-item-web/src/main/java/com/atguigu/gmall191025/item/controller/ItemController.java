package com.atguigu.gmall191025.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall191025.bean.SkuInfo;
import com.atguigu.gmall191025.bean.SkuSaleAttrValue;
import com.atguigu.gmall191025.bean.SpuSaleAttr;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    public String item (@PathVariable String skuId, HttpServletRequest request) {
        //商品信息
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo", skuInfo);

        //属性和属性值
        List<SpuSaleAttr> saleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
//        System.out.println("saleAttrList:++++++++++++++++++++++++++++++++"+saleAttrList);
        request.setAttribute("saleAttrList", saleAttrList);

        //通过spuId获取查询销售属性id
//        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //第一种方法
//        HashMap<String, String> hashMap = new HashMap<>();
//
//        String key = "";
//
//        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
//            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
//
//            if (key.length() > 0) {
//                key += "|";
//            }
//            key += skuSaleAttrValue.getSaleAttrValueId();
//
//            //循环长度和集合长度相等，数据库表已经比较完了，或者上一个skuid和下一个skuid相等时，此时把key放入map中
//            if ((i + 1) == skuSaleAttrValueListBySpu.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i + 1).getSkuId())){
//                hashMap.put(key, skuSaleAttrValue.getSkuId());
//                //清空key
//                key = "";
//            }
//        }
        //方法二
        Map map = manageService.getSkuValueIdsMap(skuInfo.getSpuId());
        String SkuJsonValues = JSON.toJSONString(map);
        request.setAttribute("SkuJsonValues",SkuJsonValues);
        return "item";
    }

}
