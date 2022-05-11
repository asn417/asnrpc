package com.asn.rpc.enums;

import lombok.Getter;

/**
 * 序列化类型枚举
 **/
public enum RpcSerializationTypeEnum {

    HESSIAN(1),PROTOBUF(2);

    @Getter
    private int type;

    RpcSerializationTypeEnum(int type){
        this.type = type;
    }

    public static RpcSerializationTypeEnum findByType(int type){
        for (RpcSerializationTypeEnum typeEnum : RpcSerializationTypeEnum.values()) {
            if (typeEnum.type == type){
                return typeEnum;
            }
        }
        //默认的序列化算法为hessian
        return HESSIAN;
    }
}
