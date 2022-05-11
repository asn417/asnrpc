package com.asn.rpc.enums;

import lombok.Getter;

public enum MessageTypeEnum {

    REQUEST(1),RESPONSE(2),HEARTBEAT(3);

    @Getter
    private int type;

    MessageTypeEnum(int type){
        this.type = type;
    }

    /**
     * 根据协议头的字段的数值，获取对应的消息类型
     **/
    public static MessageTypeEnum findByType(int type){
        for (MessageTypeEnum typeEnum : MessageTypeEnum.values()) {
            if (typeEnum.type == type){
                return typeEnum;
            }
        }
        return null;
    }
}
