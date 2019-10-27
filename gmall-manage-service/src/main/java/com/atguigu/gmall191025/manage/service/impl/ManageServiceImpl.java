package com.atguigu.gmall191025.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall191025.bean.*;
import com.atguigu.gmall191025.manage.mapper.*;
import com.atguigu.gmall191025.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {

        BaseCatalog2 baseCatalog2=new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);


        return baseCatalog2Mapper.select(baseCatalog2);

    }

    @Override
    public List<BaseCatalog3> getCatalog3(String Catalog2Id) {

        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(Catalog2Id);

        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {

        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);

        return baseAttrInfoMapper.select(baseAttrInfo);
    }


    /**
     *添加属性属性值与修改属性属性值保存的方法：
     *添加时：
     * 1.应先保存属性
     * 2.通过属性获取属性值的集合
     * 3.判断集合里是否为空，再遍历集合
     * 4.BaseAttrValue表里还有attrId也要一起保存
     * 修改时：
     * 1.判断是修改还是添加方法
     * 2.把原属性值全部清空
     * @param baseAttrInfo
     */
    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //先判断是修改方法还是添加方法，判断主键是否为空
        if(!StringUtils.isEmpty(baseAttrInfo.getId())){
            //修改
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }else {
            //保存BaseAttrInfo的值
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        //如何处理属性下的属性值? 把原属性值全部清空
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueDel);

        //通过baseAttrInfo获取AttrValueList集合的值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        //判断attrValueList集合中的值是否为空
        //若先判断后面的条件可能会报空指针
        if(attrValueList != null && attrValueList.size()>0){
            //满足条件遍历集合
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //要把AttrId也保存到表中
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                //保存
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
    }

    /**
     * 业务逻辑：
     * 1.把baseAttrValue中的数据封装到BaseAttrInfo中
     * 2.连同BaseAttrInfo里的数据一起返回到前端页面
     * @param attrId
     * @return
     */

    @Transactional
    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {

        //attrId再数据库中BaseattrValue表中的字段就是BaseAttrInfo表中的主键Id
        //查询出的结果封装成一个对象
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);

        //创建baseAttrValue对象
        BaseAttrValue baseAttrValue = new BaseAttrValue();

        //把attrId set到表中（通过baseAttrInfo对象得到主键Id）
        //这样下一步就可以根据baseAttrValue表中的attr字段查询表中的对象
        baseAttrValue.setAttrId(baseAttrInfo.getId());

        //再查询baseAttrValue中的对象
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);

        //把查询出来的对象属性值放入baseAttrInfo对象中统一返回
        baseAttrInfo.setAttrValueList(baseAttrValueList);

        //返回结果
        return baseAttrInfo;
    }


}
