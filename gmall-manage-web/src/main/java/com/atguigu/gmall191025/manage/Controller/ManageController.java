package com.atguigu.gmall191025.manage.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall191025.bean.*;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id){

        return manageService.getAttrList(catalog3Id);
    }

    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(
            @RequestBody BaseAttrInfo baseAttrInfo){

        manageService.saveAttrInfo(baseAttrInfo);

    }


    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){

        //select * from baseAttrInfo where Id = attrId
        //这样就查询到了baseAttrInfo中的平台属性名
        //再通过属性名找属性值baseattrInfo.getAttrValueList

        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);

        return attrInfo.getAttrValueList();
    }
}
