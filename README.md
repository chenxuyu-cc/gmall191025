# gmall191025
gmall191025

day01：通用mapper讲解

day02: 项目环境搭建
需求：
    1.实现查询所有用户信息
    2.用户在下订单的时候，获取用户的收货地址列表
    (可以根据用户Id查询用户地址所对应的业务)。
需要注意的点：
    1.mapper通常写在服务层，控制器层通常只写Controller
    2.控制器层与业务分离，依赖注入后，启动服务器会报错，这时就应引入dubbo
    然后引入新的注解@service和@refence还有两个配置，注解都属于com.alibaba.dubbo包下。

day03: 后台功能开发
    1.创建了两个Springboot项目
        ​	服务层：gmall-manage-service
        ​	控制器层：gmall-manage-web 
       一共写了六个功能，获取下拉框分类类别，还有商品属性和属性值的添加修改
    2.写功能时一定要符合业务逻辑，在数据回显时应该根据属性查询属性值再进行回显，不因该直接从数据库中查询属性值  
    
day04：后台功能开发
    1.文件上传功能
        利用fdfs做文件上传
    2.获取baseSaleAttr功能
    3.保存功能
    主要流程与上一天类似，代码逻辑基本一致
    
day05：后台功能开发
    1.回显平台属性--平台属性值
    2.回显销售属性--销售属性值
    3.回显商品spu图片
    4.保存SKU
    PS：回显属性时用到了mybatis（半自动的ORM框架），多表联合查询，再编写mapper.xml文件时，
    一定要弄清每个引号里的引用地址，方法，实体类时什么，mybatis配置文件要有，application配置文件要配置mapper扫描

day06：功能开发
    1.商品详情页面信息回显，进行页面渲染，以json的形式传到前端，整合thymeleaf
    2.难点，回显销售属性和销售属性值	spuSaleAttr	spuSaleAttrValue skuSaleAttrValue
    3.难点2，用户点击不同的销售属性值时，需要切换商品
    
    

