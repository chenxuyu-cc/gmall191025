package com.atguigu.gmall191025.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall191025.bean.*;
import com.atguigu.gmall191025.service.ListService;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    /**
     * 查询spu图片
     * @param spuImage
     * @return
     */
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage){
        return manageService.getSpuImageList(spuImage);
    }

    /**
     *获取销售属性
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){

        return manageService.getSpuSaleAttrList(spuId);
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){

        manageService.saveSkuInfo(skuInfo);
    }

    @RequestMapping("onSale")
    public void onSale(String skuId){

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        SkuLsInfo skuLsInfo = new SkuLsInfo();

        BeanUtils.copyProperties(skuInfo,skuLsInfo);

         listService.SkuLsInfo(skuLsInfo);
    }
}
