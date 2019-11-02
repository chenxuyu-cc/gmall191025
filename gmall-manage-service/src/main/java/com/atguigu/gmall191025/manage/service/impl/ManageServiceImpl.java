package com.atguigu.gmall191025.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall191025.config.RedisUtil;
import com.atguigu.gmall191025.bean.*;
import com.atguigu.gmall191025.manage.constant.ManageConst;
import com.atguigu.gmall191025.manage.mapper.*;
import com.atguigu.gmall191025.service.ManageService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {

        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
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

        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id(catalog3Id);
    }


    /**
     * 添加属性属性值与修改属性属性值保存的方法：
     * 添加时：
     * 1.应先保存属性
     * 2.通过属性获取属性值的集合
     * 3.判断集合里是否为空，再遍历集合
     * 4.BaseAttrValue表里还有attrId也要一起保存
     * 修改时：
     * 1.判断是修改还是添加方法
     * 2.把原属性值全部清空
     *
     * @param baseAttrInfo
     */
    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //先判断是修改方法还是添加方法，判断主键是否为空
        if (!StringUtils.isEmpty(baseAttrInfo.getId())) {
            //修改
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        } else {
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
        if (attrValueList != null && attrValueList.size() > 0) {
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
     *
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

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {

        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        spuInfoMapper.insertSelective(spuInfo);

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();

        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();

        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }

            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {

        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(checkListIsnull(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(checkListIsnull(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(checkListIsnull(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }

    }

    /**
     * 获取商品详情
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(String skuId) {

        return getSkuInfoRedisson(skuId);
    }

    public SkuInfo getSkuInfoRedisson(String skuId) {
        Jedis jedis = null;
        SkuInfo skuInfo = null;
        try {
            jedis = redisUtil.getJedis();
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            String skuJson = jedis.get(skuKey);
            if(skuJson == null){
                //创建config类
                Config config = new Config();
                //redis连接地址
                config.useSingleServer().setAddress("redis://192.168.127.131:6379");
                //初始化
                RedissonClient redissonClient = Redisson.create(config);
                //设置锁
                RLock myLock = redissonClient.getLock("myLock");
                //设置过期时间
                boolean res = false;
                try {
                    res = myLock.tryLock(100, 10, TimeUnit.SECONDS);
                    if(res){
                        skuInfo = getSkuInfoDB(skuId);
                        String skuRedisJson = JSON.toJSONString(skuInfo);
                        jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT,skuRedisJson);
                        return skuInfo;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    myLock.unlock();
                }
            }else {
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }

        return getSkuInfoDB(skuId);
    }

    public SkuInfo getSkuInfoRedisSet(String skuId) {
        Jedis jedis = null;
        SkuInfo skuInfo = null;
        try {
            //获取jedis
            jedis = redisUtil.getJedis();
            //定义一个skuKey
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            //从缓存中获取skuKey
            String skuJson = jedis.get(skuKey);
            if(skuJson == null){
                //从数据库中查询，先上锁
                /*
                    上锁的话时 set k1 v1 px 10000 nx
                */
                //k1
                String skuInfoLock = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                //v1
                String token = UUID.randomUUID().toString().replace("-","");
                //result结果
                String result = jedis.set(skuInfoLock,token,"NX","PX",ManageConst.SKULOCK_EXPIRE_PX);
                //判断结果
                if("OK".equals(result)){
                    //开始从数据库中查询
                    skuInfo = getSkuInfoDB(skuId);
                    //转换Json
                    String skuRedisJson = JSON.toJSONString(skuInfo);
                    //redis里String方法
                    jedis.setex(skuKey,ManageConst.SKULOCK_EXPIRE_PX,skuRedisJson);
                    //删除
                    //lowr脚本，判断key value是否相等
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 如果key 与value 相等则删除
                    jedis.eval(script, Collections.singletonList(skuInfoLock),Collections.singletonList(token));
                }else {
                    //不是OK,就等10s再调用方法
                    TimeUnit.SECONDS.sleep(10);
                    return getSkuInfo(skuId);
                }
            }else{
                //有缓存时
                skuInfo = JSON.parseObject(skuJson,SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    public SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);

        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {

        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {

        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public Map getSkuValueIdsMap(String spuId) {
        List<Map> mapList = skuSaleAttrValueMapper.getSaleAttrValuesBySpu(spuId);

        HashMap<Object, Object> map = new HashMap<>();
        for (Map skuMap : mapList) {
            map.put(skuMap.get("value_ids"),skuMap.get("sku_id"));
        }

        return map;
    }

    public <T> boolean  checkListIsnull(List<T> skuAttrValueList) {
        boolean flag = false;
        if (skuAttrValueList!=null && skuAttrValueList.size()>0){
            flag = true;
        }
        return flag;
    }


}
