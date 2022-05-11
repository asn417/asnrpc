package com.asn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 协议包
 **/
@Data
public class RpcProtocol<T> implements Serializable {
    //协议头
    private MessageHeader header;
    //协议体
    private T body;
}
