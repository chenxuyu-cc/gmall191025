package com.atguigu.gmall191025.config;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall191025.util.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("1111111111");
        String token = request.getParameter("newToken");

        if(token != null){
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }

        //当登录以后，访问了其他的业务，此时的newToken是从第14行代码中拿不到的
        //因为请求地址变了，例如商品详情item.gmall.com//40.html
        if(token == null){
            //此时可能在cookie中
            token = CookieUtil.getCookieValue(request,"token",false);
        }

        //此时该获取用户昵称，所以token不为空
        if(token!=null){
            Map map = getUserMapByToken(token);
            String nickName = (String)map.get("nickName");
            request.setAttribute("nickName",nickName);
        }


        //获取方法上的注解
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);

        if(methodAnnotation != null){
            //获取盐
            String salt = request.getHeader("X-forwarded-for");
            //调用认证方法，看是否有用户登录,一个请求地址
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&salt=" + salt);
            if("success".equals(result)){
                //true说明已经登录，获取token中的userId
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId",userId);

                return true;
            }else {
                //此时没有
                if(methodAnnotation.autoRedirect()){
                    //有当前注解时必须登录，没登录，重定向到登录页面
                    //要知6j定向到登录页面（假如在商品详情页被拦截了）
                    //获取当前被拦截的的url
                    String requestURL = request.getRequestURL().toString();
                    //进行编码处理
                    String encodeURL= URLEncoder.encode(requestURL,"UTF-8");
                    //重定向加拼接形成登录页的url地址
                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);

                    return false;
                }
            }
        }
        return true;
    }

    //解密token，这里没有key，用base64解码方式
    private Map getUserMapByToken(String token) {
        //token是由三段组成的，我们要获取中间的一段
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        //获取base64对象
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decodeBytes = base64UrlCodec.decode(tokenUserInfo);
        //字节数组转字符串
        String strJson = new String(decodeBytes);
        //再转map
        return JSON.parseObject(strJson, Map.class);
    }

}
