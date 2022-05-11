package com.asn.rpc.core;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异步调用的返回接口
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcFuture<T> {
    //接收异步调用的结果
    private Promise<T> promise;
    //异步调用的超时时间
    private long timeout;
}
