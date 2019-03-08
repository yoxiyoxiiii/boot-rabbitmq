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
        /**
         * 第一个参数deliveryTag：发布的每一条消息都会获得一个唯一的deliveryTag，(任何channel上发布的第一条消息的deliveryTag为1，此后的每一条消息都会加1)，deliveryTag在channel范围内是唯一的
          第二个参数multiple：批量确认标志。如果值为true，则执行批量确认，此deliveryTag之前收到的消息全部进行确认; 如果值为false，则只对当前收到的消息进行确认
         */
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        //拒绝接收消息，并设置 消息不再返回队列
        /**
         * 第一个参数deliveryTag：发布的每一条消息都会获得一个唯一的deliveryTag，deliveryTag在channel范围内是唯一的
         第二个参数requeue：表示如何处理这条消息，如果值为true，则重新放入RabbitMQ的发送队列，如果值为false，则通知RabbitMQ销毁这条消息
         */
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);

        /**
         * 批量拒绝
         * 第一个参数deliveryTag：发布的每一条消息都会获得一个唯一的deliveryTag，deliveryTag在channel范围内是唯一的
         第二个参数multiple：批量确认标志。如果值为true，包含本条消息在内的、所有比该消息deliveryTag值小的 消息都被拒绝了（除了已经被 ack 的以外）;如果值为false，只拒绝三本条消息
         第三个参数requeue：表示如何处理这条消息，如果值为true，则重新放入RabbitMQ的发送队列，如果值为false，则通知RabbitMQ销毁这条消息
         */
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),true, false);
//        int i = 1/0;

    }

}
