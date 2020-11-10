package com.changgou.pay.mq;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MQConfig {

    @Autowired
    private Environment env;

    /**
     * 创建队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(env.getProperty("mq.pay.queue.order"));
    }


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public Exchange orderExchange() {
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true, false);
    }

    /**
     * 队列绑定交换机
     *
     * @return
     */
    @Bean
    public Binding orderQueueExchange(Queue orderQueue, Exchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.key")).noargs();
    }


    /********************************秒杀订单*************************/

    /**
     * 创建队列
     */
    @Bean
    public Queue orderSeckillQueue() {
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"));
    }


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public Exchange orderSeckillExchange() {
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"), true, false);
    }

    /**
     * 队列绑定交换机
     *
     * @return
     */
    @Bean
    public Binding orderSeckillExchangeQueueExchange(Queue orderSeckillQueue, Exchange orderSeckillExchange) {
        return BindingBuilder.bind(orderSeckillQueue).to(orderSeckillExchange).with(env.getProperty("mq.pay.routing.seckillkey")).noargs();
    }
}
