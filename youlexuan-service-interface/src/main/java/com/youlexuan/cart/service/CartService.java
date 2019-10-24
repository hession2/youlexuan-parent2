package com.youlexuan.cart.service;

import com.youlexuan.entity.Cart;

import java.util.List;

/**购物车接口
 * @author 王大亮
 * @date 2019/10/17 16:18
 */
public interface CartService {

    /**
     * 增加商品到购物车
     * @param cartList 原有的商家购物车集合(cookie中或者是redis中获取的)
     * @param itemId 添加的商品的id
     * @param num 添加的数量
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 根据用户名从redis中取购物车集合
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 往redis中存购物车集合
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    // 合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
