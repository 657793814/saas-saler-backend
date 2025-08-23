package com.liuzd.soft.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuzd.soft.config.RabbitMQConfig;
import com.liuzd.soft.entity.TUserEntity;
import com.liuzd.soft.mq.msg.UserMsgDto;
import com.liuzd.soft.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author: liuzd
 * @date: 2025/8/20
 * @email: liuzd2025@qq.com
 * @desc
 */
@Service
@RequiredArgsConstructor
public class UserMQConsumerService {

    final UserServiceImpl userServiceImpl;
    final ObjectMapper objectMapper;

    /**
     * 监听并处理对象消息
     *
     * @param message 对象消息
     */
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_NAME)
    public void handleObjectMessage(UserMsgDto message) {
        System.out.println("接收到对象消息: " + message);
        // 处理接收到的对象消息
        processObjectMessage(message);
    }

    private void processObjectMessage(UserMsgDto<TUserEntity> message) {
        // 实现对象消息处理逻辑
        System.out.println("处理对象消息: " + message);
        if (message.getEventType() == UserMsgDto.EventType.DEL.getValue()) {
            TUserEntity userEntity = objectMapper.convertValue(message.getInfo(), TUserEntity.class);
            userServiceImpl.clearUserToken(userEntity.getOpenid());
        }

    }
}
