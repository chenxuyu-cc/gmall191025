package com.atguigu.gmall191025.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall191025.bean.BaseAttrInfo;
import com.atguigu.gmall191025.bean.BaseAttrValue;
import com.atguigu.gmall191025.bean.SkuLsParams;
import com.atguigu.gmall191025.bean.SkuLsResult;
import com.atguigu.gmall191025.service.ListService;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
//    @ResponseBody
    public String getList(SkuLsParams skuLsParams, HttpServletRequest request) {
        //分页
        skuLsParams.setPageSize(2);

        SkuLsResult skuLsResult = listService.search(skuLsParams);

        //显示平台属性，平台属性值！
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        //通过valueId查询
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(attrValueIdList);

        //通过平台值过滤
        String urlParam = makeUrlParam(skuLsParams);

        //存储面包屑的集合
        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<>();

        //使选中的平台属性和平台属性值消失，要用迭代器循环，遍历平台属性
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo = iterator.next();
            //获取平台属性值集合
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            //判断valueId是否为空
            if(skuLsParams.getValueId() != null && skuLsParams.getValueId().length>0){
                //遍历平台属性值集合
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    //遍历valueId
                    for (String valueId : skuLsParams.getValueId()) {
                        //判断是否相等
                        if(valueId.equals(baseAttrValue.getId())){
                            iterator.remove();

                            //平台属性：平台属性值
                            BaseAttrValue baseAttrValues = new BaseAttrValue();
                            baseAttrValues.setValueName(baseAttrInfo.getAttrName()+""+baseAttrValue.getValueName());

                            //调用makeUrlParam方法,生成一个新的urlParam，放入到对象中
                            String newUrlParam = makeUrlParam(skuLsParams, valueId);
                            baseAttrValues.setUrlParam(newUrlParam);

                            //放入面包屑中
                            baseAttrValueArrayList.add(baseAttrValues);
                        }
                    }
                }
            }
        }
        request.setAttribute("pageNo",skuLsParams.getPageNo());
        request.setAttribute("totalPages",skuLsResult.getTotalPages());
        request.setAttribute("baseAttrValueArrayList", baseAttrValueArrayList);
        request.setAttribute("urlParam", urlParam);
        request.setAttribute("baseAttrInfoList", baseAttrInfoList);
        request.setAttribute("skuLsInfoList", skuLsResult.getSkuLsInfoList());

//      return JSON.toJSONString(search);
        return "list";
    }

    private String makeUrlParam(SkuLsParams skuLsParams,String... excludeValueIds) {
        String urlParam = "";

        //http://list.gmall.com/list.html?catalog3Id=61
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            urlParam += "catalog3Id=" + skuLsParams.getCatalog3Id();
        }

        //http://list.gmall.com/list.html?keyword=手机
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            urlParam += "keyWord=" + skuLsParams.getKeyword();
        }

        //判断是否有平台属性id
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (String valueId : skuLsParams.getValueId()) {
                if(excludeValueIds != null && excludeValueIds.length>0){
                    //获取上一个传入的valueId
                    String excludeValueId = excludeValueIds[0];
                    //相等不走，跳出
                    if(excludeValueId.equals(valueId)){
                        continue;
                    }
                }
                if (urlParam.length() > 0) {
                    urlParam += "&";
                }
                urlParam += "valueId=" + valueId;
            }
        }
        return urlParam;
    }
}
