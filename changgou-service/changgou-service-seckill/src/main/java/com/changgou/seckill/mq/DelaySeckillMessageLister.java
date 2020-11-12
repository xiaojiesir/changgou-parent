package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.SystemConstants;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RabbitListener(queues = "seckillQueue")
public class DelaySeckillMessageLister {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @RabbitHandler
    public void getDelayMessage(String message) throws Exception {
        System.out.println("回滚数据=======" + new Date());
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);
        Object UserQueueStatus = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(seckillStatus.getUsername());
        if (UserQueueStatus != null) {
            //Todo
        }
    }
}
