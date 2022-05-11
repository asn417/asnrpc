package com.asn.rpc.serialization.factory;

import com.asn.rpc.enums.RpcSerializationTypeEnum;
import com.asn.rpc.serialization.RpcSerialization;
import com.asn.rpc.serialization.impl.HessianSerializationImpl;

/**
 * 序列化工厂
 **/
public class RpcSerializationFactory {

    public static RpcSerialization getRpcSerialization(byte type){
        RpcSerializationTypeEnum serializationType = RpcSerializationTypeEnum.findByType(type);
        switch (serializationType){
            case HESSIAN:
                return new HessianSerializationImpl();
            default:
                throw new IllegalArgumentException("serialization type illegal "+type);
        }
    }
}
