package com.youlexuan.page.service;

/**商品详细页接口
 * @author 王大亮
 * @date 2019/10/12 16:25
 */
public interface ItemPageService {


    /**
     * 生成商品详细页
     * @param goodsId
     */
    public boolean genItemHtml(Long goodsId);

}
