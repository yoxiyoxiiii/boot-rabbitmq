package com.example.bootrabbitmq.callback;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{


    @Autowired
    private RabbitTemplate  rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    /**
     * rabbit mq 消息确认机制
     * 1 消息是否正确投递到 exchange 回调
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        //消息成功投递到exchange
//        if (ack) {
//            System.out.println(ack);
//        } else {//失败
//            System.err.println(ack);
//        }
    }

    /**
     *rabbit mq 消息确认机制
     * 2 消息如果没有成功投递到 队列 ，回调
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.err.println(replyCode);
    }
}
