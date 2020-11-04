package com.changgou.order.mq.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.order}")
public class OrderMessageListener {


    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void handlerData(String msg) throws ParseException {
        //1.接收消息(有订单的ID  有transaction_id )
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        System.out.println("监听到的消息" + map);
        //2.更新对营的订单的状态
        if (map != null) {
            if (map.get("return_code").equalsIgnoreCase("success")) {
                if (map.get("result_code").equalsIgnoreCase("success")) {
                    orderService.updateStatus(map.get("out_trade_no"), map.get("time_end"), map.get("transaction_id"));
                }

            } else {
                //删除订单 支付失败.....
                orderService.deleteOrder(map.get("out_trade_no"));
            }
        }
    }
}
