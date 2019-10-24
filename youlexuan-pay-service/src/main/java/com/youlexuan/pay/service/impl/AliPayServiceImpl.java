package com.youlexuan.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.youlexuan.mapper.TbOrderMapper;
import com.youlexuan.mapper.TbPayLogMapper;
import com.youlexuan.pay.service.AliPayService;
import com.youlexuan.pojo.TbOrder;
import com.youlexuan.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 王大亮
 * @date 2019/10/21 16:28
 */
@Service(timeout = 10000)
public class AliPayServiceImpl implements AliPayService{

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号
     * @param total_amount 金额
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_amount) {
        Map<String,String> map=new HashMap<String, String>();
        //创建预下单请求对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+out_trade_no+"\"," +
                "    \"total_amount\":\""+total_amount+"\"," +
                "    \"subject\":\"测试购买商品001\"," +
                "    \"store_id\":\"xa_001\"," +
                "    \"timeout_express\":\"90m\"}");//设置业务参数
        //发出预下单业务请求
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            //从相应对象读取相应结果
            String code = response.getCode();
            System.out.println("响应码:"+code);
            //全部的响应结果
            String body = response.getBody();
            System.out.println("返回结果:"+body);

            if(code.equals("10000")){
                map.put("qrcode", response.getQrCode());
                map.put("out_trade_no", response.getOutTradeNo());
                map.put("total_amount",total_amount);
                System.out.println("qrcode:"+response.getQrCode());
                System.out.println("out_trade_no:"+response.getOutTradeNo());
                System.out.println("total_amount:"+total_amount);
            }else{
                System.out.println("预下单接口调用失败:"+body);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        return map;

    }

    /**
     * 交易查询接口alipay.trade.query：
     * 获取指定订单编号的，交易状态
     * @throws AlipayApiException
     */
    @Override
    public  Map<String,String> queryPayStatus(String out_trade_no){
        Map<String,String> map=new HashMap<String, String>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+out_trade_no+"\"," +
                "    \"trade_no\":\"\"}"); //设置业务参数

        //发出请求
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            String code=response.getCode();
            System.out.println("返回值1:"+response.getBody());
            if(code.equals("10000")){

                //System.out.println("返回值2:"+response.getBody());
                map.put("out_trade_no", out_trade_no);
                map.put("trade_no", response.getTradeNo());
                map.put("tradestatus", response.getTradeStatus());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return map;

    }
    /**
     * 修改订单状态
     * @param out_trade_no 支付订单号
     * @param trade_no 微信返回的交易流水号 transaction_id一样
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String trade_no) {

          //1.修改支付日志状态
            TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
            payLog.setPayTime(new Date());
            payLog.setTradeState("1");//已支付
            payLog.setTransactionId(trade_no);//交易号
            payLogMapper.updateByPrimaryKey(payLog);
          //2.修改关联的订单的状态
            String orderList = payLog.getOrderList();//获取订单号列表
            String[] orderIds = orderList.split(",");//获取订单号数组

            for(String orderId:orderIds){
                TbOrder order = orderMapper.selectByPrimaryKey( Long.parseLong(orderId) );
                if(order!=null){
                    order.setStatus("2");//已付款
                    orderMapper.updateByPrimaryKey(order);
                }
            }
          //3.清除缓存中的支付日志对象
            redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }
    /**
     * 关闭订单接口
     */
    @Override
    public Map closePay(String out_trade_no) {
        Map<String,String> map=new HashMap<String, String>();
        //撤销交易请求对象
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\""+out_trade_no+"\"," +
                "    \"trade_no\":\"\"}"); //设置业务参数

        try {
            AlipayTradeCancelResponse response = alipayClient.execute(request);
            String code=response.getCode();

            if(code.equals("10000")){

                System.out.println("返回值:"+response.getBody());
                map.put("code", code);
                map.put("out_trade_no", out_trade_no);
                return map;
            }
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return null;
    }


}
