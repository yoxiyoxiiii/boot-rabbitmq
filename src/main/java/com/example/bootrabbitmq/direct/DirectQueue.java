package com.example.bootrabbitmq.direct;

import org.springframework.amqp.core.*;
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

    @GetMapping("send/direct")
    public String sendirect() {
        //声明了直连exchange ，必须指定
        amqpTemplate.convertAndSend("directExchange","hello-direct","hello-direct");
        return "success";
    }

    //声明一个队列
    @Bean
    public Queue queue() {
        return new Queue("hello");
    }

    /**
     * 声明一个直连队列
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("directExchange");
    }

    /**
     * 队列和 直连exchange 绑定
     * @param directExchange
     * @param queue
     * @return
     */
    @Bean
    public Binding binding(DirectExchange directExchange,
                           Queue queue) {

        return BindingBuilder.bind(queue).to(directExchange).with("hello-direct");

    }


    /**
     * 消息消费者
     * @param msg
     * @RabbitListener(queues = "hello") 监听的队列
     */
    @RabbitListener(queues = "hello")
    @RabbitHandler
    public void revc(String msg) {
        System.out.println(msg);
    }

}
