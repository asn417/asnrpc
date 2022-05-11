package com.asn.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装客户端远程调用的请求信息
 * 因为序列化使用hessian，所以这里也需要实现Serializable接口
 **/
@Data
public class RpcRequest implements Serializable {

    //调用的类名称
    private String className;
    //调用的方法名
    private String methodName;
    //方法参数类型
    private Class<?>[] paramTypes;
    //方法参数
    private Object[] params;
    //服务版本
    private String version;
}
