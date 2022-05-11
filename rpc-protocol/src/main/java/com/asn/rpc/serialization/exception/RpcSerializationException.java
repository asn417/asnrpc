package com.asn.rpc.serialization.exception;

/**
 * 自定义异常类
 **/
public class RpcSerializationException extends RuntimeException{

    private static final long serialVersionUID = 6666L;

    public RpcSerializationException(String msg){
        super(msg);
    }

    public RpcSerializationException(Throwable cause){
        super(cause);
    }
}
