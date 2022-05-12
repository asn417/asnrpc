package com.asn.rpc.provider.processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 为业务请求处理分配业务线程池
 **/
public class RpcRequestProcessor {

    private static volatile ThreadPoolExecutor threadPool;

    public static void submit(Runnable task){
        if (threadPool == null){
            synchronized (RpcRequestProcessor.class){
                if (threadPool == null){
                    threadPool = new ThreadPoolExecutor(8,8,30, TimeUnit.SECONDS,new LinkedBlockingQueue<>(1000));
                }
            }
        }
        threadPool.submit(task);
    }
}
