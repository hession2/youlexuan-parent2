package com.youlexuan.pay.service;

import java.util.Map;

/**
 * @author 王大亮
 * @date 2019/10/21 15:37
 *
 *     支付宝接口
 */
public interface AliPayService {

    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号
     * @param total_amount 金额
     * @return
     */
    public Map createNative(String out_trade_no, String total_amount);

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 修改订单状态
     * @param out_trade_no 支付订单号
     * @param transaction_id 微信返回的交易流水号
     */
    public void updateOrderStatus(String out_trade_no,String transaction_id);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);


}
