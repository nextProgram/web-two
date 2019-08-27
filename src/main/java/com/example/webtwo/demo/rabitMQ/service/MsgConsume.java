package com.example.webtwo.demo.rabitMQ.service;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 从rabbitMQ消费消息
 * 注解方式，配置方式，此处使用注解方式
 * @RabbitListener
 * @author lhx
 * @date 2019/6/20
 */
@Service
public class MsgConsume {
    private final static Logger logger = LoggerFactory.getLogger(MsgConsume.class);

    /**
     * 接收消息-不指定bindking
     * @RabbitHandler:指定对消息的处理方法；
     * @RabbitListener(queues = "myQueue")不能自动创建队列,只是表示对队列myQueue进行监听
     * @RabbitListener(queuesToDeclare = @Queue("myQueue"))表示自动创建队列
     * 如下的写法表示：自动创建Queue、Exchange和Binding
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("myQueue"),
            exchange = @Exchange("myExchange")
    ))
    public void getMsg(String message){
        logger.info("------>Consumer Receive:" + message);
    }
    /**
     * 接收消息-新建computer队列来接受computer信息
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange("myOrder"),
            key = "computer",
            value = @Queue("computerOrder")
    ))
    public void processComputer(String message) {
        //接受消息
        logger.info("computer MqReceiver: {}", message);
    }

    /**
     * 接收消息-新建fruit队列来接受fruit信息
     * 如果直接获取消息可以这么写：public void processFruit(String message)，不用加channel,其中，参数名称不一定非得用message
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            key = "fruit",
            exchange = @Exchange("myOrder"),
            value = @Queue("fruitOrder")
    ))
    public void processFruit(Message message, Channel channel) {
        try {
            //消费消息
            String msg = new String(message.getBody());
            logger.info("fruit MqReceiver: {}", msg);
            //采用手动应答模式, 手动确认应答更为安全稳定
            //rabbitmq默认的处理方式为auto ack，意味着从消息队列取出一个消息时，ack自动发送，mq就会将消息删除
            //true表示采用自动回执模式，false表示采用手动回执模式（手动ack），不进行手动ack则mq不做删除
            //在消息处理成功后发送ack确认，或失败后发送nack使信息重新投递
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.info("send message failed: {}" ,e1.getMessage());
            }
        }
    }
}
