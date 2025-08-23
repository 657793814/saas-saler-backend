package com.liuzd.soft.mq.msg;

import lombok.Data;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */
@Data
public class UserMsgDto<T> {

    int eventType = 0; //1 新增用户，2 修改用户，3 修改用户状态
    T info;

    public enum EventType {
        ADD(1),
        EDIT(2),
        DEL(3);
        private int value;

        public int getValue() {
            return value;
        }

        EventType(int value) {
            this.value = value;
        }
    }

}
