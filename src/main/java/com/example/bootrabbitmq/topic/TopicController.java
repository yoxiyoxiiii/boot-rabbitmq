package com.example.bootrabbitmq.topic;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * @author Administrator
 */
@RestController
public class TopicController {

    @Bean
    public Queue topicQueue() {
        return new Queue("topicQueue");
    }

    /**
     * 创建topic 类型的exchange
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topicExchange");
    }

    /**
     * 绑定 队列和exchange ，并设置 路由关键字
     * @param topicExchange
     * @param topicQueue
     * @return
     */
    @Bean
    public Binding topicBinding(TopicExchange topicExchange,
                           Queue topicQueue) {
        //hello.* : * 代表一个单词 --》hello.whghgh 可以投递
        //hello.# ：# 代表多个或零个单词 --》hello.whghgh.hhh 可以投递
        return BindingBuilder.bind(topicQueue).to(topicExchange).with("topic.*");
    }


    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("send/topic")
    public String send() {
        amqpTemplate.convertAndSend("topicExchange","topic.hhh","topicExchangex");
        return "success";

    }

    /**
     * 两个消费者同时消费同一个队列
     * 默认 是 轮询分发
     * @param msg
     */
    @RabbitListener(queues = "topicQueue")
    @RabbitHandler
    public void recv(String msg, com.rabbitmq.client.Channel channel) throws IOException {
        //消费者可以设置 一次消费的消息数量，在手动ack下，如果没有ack返回，则不会再投递下一条消息给该消费者
        //该方式也可以用与消费者端的限流
        channel.basicQos(1);
        System.err.println(msg+"recv");
    }

    @RabbitListener(queues = "topicQueue")
    @RabbitHandler
    public void recv2(String msg, com.rabbitmq.client.Channel channel) throws IOException {
        channel.basicQos(1);
        System.err.println(msg+"recv2");
    }


}
