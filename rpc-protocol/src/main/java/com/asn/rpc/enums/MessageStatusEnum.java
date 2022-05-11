package com.asn.rpc.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 **/
public enum MessageStatusEnum {

    SUCCESS(1),FAIL(2);

    @Getter
    private int type;

    MessageStatusEnum(int type){
        this.type = type;
    }

    public static MessageStatusEnum findByType(int type){
        for (MessageStatusEnum typeEnum : MessageStatusEnum.values()) {
            if (typeEnum.type == type){
                return typeEnum;
            }
        }
        return null;
    }
}
