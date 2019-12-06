package com.atguigu.gmall191025.order.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall191025.bean.*;
import com.atguigu.gmall191025.config.LoginRequire;
import com.atguigu.gmall191025.enums.OrderStatus;
import com.atguigu.gmall191025.enums.ProcessStatus;
import com.atguigu.gmall191025.service.CartService;
import com.atguigu.gmall191025.service.ManageService;
import com.atguigu.gmall191025.service.OrderService;
import com.atguigu.gmall191025.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class OrderController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

    @Reference
    private ManageService manageService;

    @RequestMapping("trade")
    @LoginRequire
    public String trade(HttpServletRequest request){

        String userId = (String) request.getAttribute("userId");
        //获取用户地址
        List<UserAddress> userAddressList = userService.getUserAddressByUserId(userId);
        //获取勾选订单列表
        List<CartInfo> cartCheckedList = cartService.getCartCheckedList(userId);
        //对拷订单信息
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());

            orderDetailList.add(orderDetail);
        }
        //总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();

        //调用方法产生流水号
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo",tradeNo);

        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        request.setAttribute("orderDetailList",orderDetailList);
        request.setAttribute("userAddressList",userAddressList);
        return "trade";
    }

    @RequestMapping("submitOrder")
    @LoginRequire
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
        //从控制器这才能取到id
        String userId = (String) request.getAttribute("userId");

        //获取流水号
        String tradeNo = request.getParameter("tradeNo");
        orderInfo.setUserId(userId);

        //判断
        boolean result = orderService.checkTradeCode(userId, tradeNo);
        if(!result){
            request.setAttribute("errMsg","请勿重复提交订单！");
            return "tradeFail";
        }

        orderService.deleteTradeCode(userId);

        //验证库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if(!flag){
                request.setAttribute("errMsg",orderDetail.getSkuName()+"库存不足");
                return "tradeFail";
            }

            //验证价格
            SkuInfo skuInfoDB = manageService.getSkuInfoDB(orderDetail.getSkuId());
            int res = orderDetail.getOrderPrice().compareTo(skuInfoDB.getPrice());
            //价格变动
            if(res !=0 ){
                request.setAttribute("errMsg",orderDetail.getSkuName()+"价格有变动！");
                cartService.loadCartCache(userId);
                return "tradeFail";
            }
            //还可以判断商品是否有优惠券，打折{时间范围}
        }

        String orderId = orderService.saveOrderInfo(orderInfo);

        return "redirect://payment.gmall.com/index?orderId="+orderId;
    }

    @RequestMapping("orderSplit")
    @ResponseBody
    public String orderSplit(HttpServletRequest request){
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");
        // 定义订单集合,拆单
        List<OrderInfo> subOrderInfoList = orderService.splitOrder(orderId,wareSkuMap);

        List<Map> wareMapList=new ArrayList<>();
        for (OrderInfo orderInfo : subOrderInfoList) {
            Map map = orderService.initWareOrder(orderInfo);
            wareMapList.add(map);
        }
        return JSON.toJSONString(wareMapList);
    }

}
