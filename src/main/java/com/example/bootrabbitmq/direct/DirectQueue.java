package com.example.bootrabbitmq.direct;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DirectQueue {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("send")
    public String send() {
        //默认到空字符串的exchange, 路由key 就是要求是 队列名
        amqpTemplate.convertAndSend("hello", "你好!");
        return "success";
    }

    //声明一个队列
    @Bean
    public Queue queue() {
        return new Queue("hello");
    }

    @RabbitListener(queues = "hello")
    @RabbitHandler
    public void revc(String msg) {
        System.out.println(msg);
    }

}
