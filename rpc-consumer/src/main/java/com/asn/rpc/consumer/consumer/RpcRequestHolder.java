package com.asn.rpc.consumer.consumer;

import com.asn.rpc.core.RpcFuture;
import com.asn.rpc.core.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 全局变量管理器
 **/
public class RpcRequestHolder {

    public static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    //全局的请求处理结果容器
    public static final Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
