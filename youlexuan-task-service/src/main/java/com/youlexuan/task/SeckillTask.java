package com.youlexuan.task;

import com.youlexuan.mapper.TbSeckillGoodsMapper;
import com.youlexuan.pojo.TbSeckillGoods;
import com.youlexuan.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王大亮
 * @date 2019/10/23 15:35
 */
@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 刷新秒杀商品
     * 每分钟执行查询秒杀商品表，将符合条件的记录并且缓存中不存在的秒杀商品存入缓存
     */
    @Scheduled(cron="0 * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行了任务调度"+new Date());
        //查询所有的秒杀商品键集合
        List ids = new ArrayList( redisTemplate.boundHashOps("seckillGoods").keys());
        //查询正在秒杀的商品列表
        TbSeckillGoodsExample example=new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//剩余库存大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
        if(ids.size()>0){
            criteria.andIdNotIn(ids);//排除缓存中已经有的商品
        }
        List<TbSeckillGoods> seckillGoodsList= seckillGoodsMapper.selectByExample(example);
        //装入缓存
        for( TbSeckillGoods seckill:seckillGoodsList ){
            redisTemplate.boundHashOps("seckillGoods").put(seckill.getId(), seckill);
        }
        System.out.println("将"+seckillGoodsList.size()+"条商品装入缓存");
    }

    /**
     * 移除秒杀商品
     * 每秒中在缓存的秒杀上皮列表中查询过期的商品，发现过期同步到数据库，并在缓存中移除该秒杀商品
     */
    @Scheduled(cron="* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("移除秒杀商品任务在执行");
        //扫描缓存中秒杀商品列表，发现过期的移除
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for( TbSeckillGoods seckill:seckillGoodsList ){
            if(seckill.getEndTime().getTime()<new Date().getTime()  ){//结束日期小于当前日期过期
                seckillGoodsMapper.updateByPrimaryKey(seckill);//向数据库保存记录
                redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());//移除缓存数据
                System.out.println("移除秒杀商品"+seckill.getId());
            }
        }
        System.out.println("移除秒杀商品任务结束");
    }
}