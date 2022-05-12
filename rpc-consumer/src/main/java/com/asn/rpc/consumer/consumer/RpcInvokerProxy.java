package com.asn.rpc.consumer.consumer;

import com.asn.rpc.constants.RpcProtocolConstants;
import com.asn.rpc.core.RegistryService;
import com.asn.rpc.core.RpcFuture;
import com.asn.rpc.core.RpcRequest;
import com.asn.rpc.core.RpcResponse;
import com.asn.rpc.enums.MessageStatusEnum;
import com.asn.rpc.enums.MessageTypeEnum;
import com.asn.rpc.enums.RpcSerializationTypeEnum;
import com.asn.rpc.protocol.MessageHeader;
import com.asn.rpc.protocol.RpcProtocol;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class RpcInvokerProxy implements InvocationHandler {

    private RegistryService registryService;
    private String version;
    private long timeout;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建自定义协议包
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        //构建消息头
        MessageHeader header = new MessageHeader();
        header.setMagic(RpcProtocolConstants.MAGIC);
        header.setVersion(RpcProtocolConstants.VERSION);
        header.setStatus((byte) MessageStatusEnum.SUCCESS.getType());
        header.setMsgType((byte) MessageTypeEnum.REQUEST.getType());
        header.setMsgId(RpcRequestHolder.ID_GENERATOR.incrementAndGet());//id自增
        header.setSerializationType((byte) RpcSerializationTypeEnum.HESSIAN.getType());
        //构建消息体
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        request.setVersion(this.version);
        //组装协议包
        protocol.setHeader(header);
        protocol.setBody(request);

        //借助RpcConsumer完成服务调用
        RpcConsumer rpcConsumer = new RpcConsumer();
        rpcConsumer.sendRequest(protocol,registryService);
        //Future异步保存响应结果
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()),timeout);
        RpcRequestHolder.REQUEST_MAP.put(header.getMsgId(), future);

        //等待结果
        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
