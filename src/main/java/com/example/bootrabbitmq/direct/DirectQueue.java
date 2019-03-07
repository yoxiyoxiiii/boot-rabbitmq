package com.example.bootrabbitmq.direct;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DirectQueue {

    @Autowired
    private RabbitTemplate amqpTemplate;

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



    /**
     * 声明死信交换机
     * @return
     */
    @Bean
    public DirectExchange deadDirectExchange() {
        return new DirectExchange("deadDirectExchange");
    }

    /**
     * 声明死信队列
     * @return
     */
    @Bean
    public Queue deadQueue() {
        return new Queue("deadQueue");
    }

    @Bean
    public Binding deadBinding (Queue deadQueue,
                                DirectExchange deadDirectExchange) {
        return BindingBuilder.bind(deadQueue).to(deadDirectExchange).with("dead");
    }

    //声明一个队列
    @Bean
    public Queue queue() {
        Map<String,Object> args = new HashMap<>();
        //为该队列设置死信exchange
        args.put("x-dead-letter-exchange","deadDirectExchange");
        args.put("x-dead-letter-routing-key","dead");
        return new Queue("hello",true,false,false,args);
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
    public void revc(String msg, Channel channel, Message message) throws IOException {
        System.out.println(msg);
//        channel.basicQos(1);
//        throw new RuntimeException("sss");
        //手动确认ack
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
//        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
        int i = 1/0;
    }

}
