package com.example.bootrabbitmq.txRbbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 使用事务投递消息，效率低
 */
@RestController
public class TxRabbitMqController {

    @Bean
    public Queue txQueue() {
        return new Queue("txQueue");
    }



    @GetMapping("send/tx")
    public String send() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        try {
            //开启事务
            channel.txSelect();
            channel.basicPublish("", "txQueue", null, "tx".getBytes());
            //提交事务
            channel.txCommit();
        } catch (Exception e) {
            //事务回滚
            channel.txRollback();
        }

        return "success";

    }
}
