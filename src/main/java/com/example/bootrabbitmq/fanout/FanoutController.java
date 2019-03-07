package com.example.bootrabbitmq.fanout;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FanoutController {

    /**
     * 声明一个 fanout 类型的 exchange
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fan");
    }

    @Bean
    public Queue fanoutQueue() {
        return new Queue("fanoutQueue");
    }

    /**
     * 将队列和 exchange 绑定
     * @param fanoutExchange
     * @param fanoutQueue
     * @return
     */
    @Bean
    public Binding exchangeBuilder(FanoutExchange fanoutExchange,
                                   Queue fanoutQueue) {

        Binding binding = BindingBuilder.bind(fanoutQueue).to(fanoutExchange);
        return binding;
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("send/fanout")
    public String sendFanout() {
        //广播类型的 exchange ，路由key 任何值
        amqpTemplate.convertAndSend("fan","xxx","fanoutExchange");
        return "success";
    }

    @RabbitListener(queues = {"fanoutQueue"})
    @RabbitHandler
    public void recv(String msg) {
        System.out.println(msg);
    }
}
