package com.asn.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义协议头
 **/
@Data
public class MessageHeader implements Serializable {

    /**
     * 魔数 2byte
     * 协议版本号 1byte
     * 序列化算法类型 1byte
     * 报文类型 1byte
     * 状态 1byte
     * 消息ID 8byte
     * 消息长度 4byte
     **/
    private short magic;
    private byte version;
    private byte serializationType;
    private byte msgType;
    private byte status;
    private long msgId;
    private int msgLength;
}
