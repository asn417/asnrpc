package com.asn.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装服务端响应给客户端的信息
 **/
@Data
public class RpcResponse implements Serializable {

    //成功是返回的数据
    private Object data;
    //失败时返回的信息
    private String message;
}
