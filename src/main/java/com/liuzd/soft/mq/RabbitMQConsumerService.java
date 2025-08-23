package com.liuzd.soft.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuzd.soft.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: liuzd
 * @date: 2025/8/20
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
public class RabbitMQConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 监听并处理消息
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(String message) {
        System.out.println("接收到消息: " + message);
        // 处理接收到的消息
        processMessage(message);
    }

    /**
     * 监听并处理对象消息
     *
     * @param message 对象消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleObjectMessage(Map<String, Object> message) {
        System.out.println("接收到对象消息: " + message);
        // 处理接收到的对象消息
        processObjectMessage(message);
    }

    private void processMessage(String message) {
        // 实现消息处理逻辑
        System.out.println("处理消息: " + message);
    }

    private void processObjectMessage(Map<String, Object> message) {
        // 实现对象消息处理逻辑
        System.out.println("处理对象消息: " + message);
    }
}
