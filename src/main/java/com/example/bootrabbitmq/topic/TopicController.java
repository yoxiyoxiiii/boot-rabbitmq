package com.example.bootrabbitmq.topic;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param queue
     * @return
     */
    @Bean
    public Binding topicBinding(TopicExchange topicExchange,
                           Queue queue) {
        //hello.* : * 代表一个单词 --》hello.whghgh 可以投递
        //hello.# ：# 代表多个或零个单词 --》hello.whghgh.hhh 可以投递
        return BindingBuilder.bind(queue).to(topicExchange).with("hello.#");
    }


    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("send/topic")
    public String send() {
        amqpTemplate.convertAndSend("topicExchange","hello.whghgh.hhh","topicExchange");
        return "success";

    }

    @RabbitListener(queues = "topicQueue")
    @RabbitHandler
    public void recv(String msg) {
        System.out.println(msg);
    }


}
