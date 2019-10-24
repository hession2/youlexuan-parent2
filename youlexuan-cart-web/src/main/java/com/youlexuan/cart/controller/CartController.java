package com.youlexuan.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.youlexuan.cart.service.CartService;
import com.youlexuan.entity.Cart;
import com.youlexuan.entity.Result;
import com.youlexuan.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 王大亮
 * @date 2019/10/17 17:35
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout=6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

            //从cookie中获取购物车集合
            String cartListString =CookieUtil.getCookieValue(request, "cartList","UTF-8");
            //判断cartListString的值
            if(cartListString==null || cartListString.equals("")){
                cartListString="[]";
            }
            //将cartListString的值返回
            List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        //没有用户登录
        if(username.equals("anonymousUser")){
            return cartList_cookie;
        }else{
            //有用户登录 从redis中取
            List<Cart> cartList_redis =cartService.findCartListFromRedis(username);

            if(cartList_cookie.size()>0){//如果本地存在购物车
                //合并购物车
                cartList_redis=cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清除本地cookie的数据
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);
            }

            return cartList_redis;
        }




    }


    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户："+username);

        try {
            List<Cart> cartList =findCartList();//获取购物车列表
            //添加商品
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            //没有用户登录
            if(username.equals("anonymousUser")){
                //新的购物车集合放进cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
                System.out.println("向cookie存入数据");
            }else{
                //由用户登录  存入redis中
                cartService.saveCartListToRedis(username,cartList);
                System.out.println("向redis存入数据");
            }



            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

}
