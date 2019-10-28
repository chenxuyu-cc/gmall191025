package com.atguigu.gmall191025.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall191025.bean.BaseSaleAttr;
import com.atguigu.gmall191025.bean.SpuInfo;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    private ManageService manageService;

    /**
     * 根据三级分类查询spu
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    public List<SpuInfo> getSpuInfoList(String catalog3Id){

        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return manageService.getSpuInfoList(spuInfo);
    }


    /**
     * //http://localhost:8082/baseSaleAttrList
     * 获取baseSaleAttr
     * @param
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){

        return manageService.getBaseSaleAttrList();
    }


    /**
     *  http://localhost:8082/saveSpuInfo
     *  保存数据
     * @param spuInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    public void  saveSpuInfo(@RequestBody SpuInfo spuInfo){

        manageService.saveSpuInfo(spuInfo);

    }
}
