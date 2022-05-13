package com.asn.rpc.provider.handler;

import com.asn.rpc.core.RpcRequest;
import com.asn.rpc.core.RpcResponse;
import com.asn.rpc.core.RpcUtil;
import com.asn.rpc.enums.MessageStatusEnum;
import com.asn.rpc.enums.MessageTypeEnum;
import com.asn.rpc.protocol.MessageHeader;
import com.asn.rpc.protocol.RpcProtocol;
import com.asn.rpc.provider.cache.LocalCache;
import com.asn.rpc.provider.processor.RpcRequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {
        //将业务请求操作提交给业务线程池处理
        RpcRequestProcessor.submit(()->{
            //创建一个返回给客户端的协议对象
            RpcProtocol<RpcResponse> rpcResponseProtocol = new RpcProtocol<>();
            //创建响应体对象
            RpcResponse rpcResponse = new RpcResponse();
            //设置响应体的报文类型
            MessageHeader header = msg.getHeader();
            byte msgType = header.getMsgType();
            //根据请求类型做不同处理，
            header.setMsgType((byte) MessageTypeEnum.RESPONSE.getType());
            //获取客户端发送的请求信息
            RpcRequest request = msg.getBody();
            try{
                //根据请求信息，调用对应的服务方法，并返回结果
                Object result = handleRequest(request);
                //设置响应结果
                rpcResponse.setData(result);
                //设置响应状态
                header.setStatus((byte) MessageStatusEnum.SUCCESS.getType());
                //设置协议对象的响应头和响应体
                rpcResponseProtocol.setHeader(header);
                rpcResponseProtocol.setBody(rpcResponse);
            }catch (Exception e){
                header.setStatus((byte) MessageStatusEnum.FAIL.getType());
                rpcResponse.setMessage(e.getMessage());
                log.error("RpcRequestHandler process request {} fail",header.getMsgId());
            }
            //给客户端发送响应结果
            ctx.writeAndFlush(rpcResponseProtocol);
        });
    }

    private Object handleRequest(RpcRequest request) throws InvocationTargetException {
        //根据请求信息，定位到具体的服务方法
        String serviceKey = RpcUtil.buildServiceKey(request.getClassName(), request.getVersion());
        Object serviceBean = LocalCache.rpcServiceCache.get(serviceKey);
        if (serviceBean == null){
            throw new RuntimeException("service not exist:[service:"+request.getClassName()+",method:"+request.getMethodName()+"]");
        }
        //这里可以使用jdk的反射方式进行方法调用，也可以使用cglib提供的FastClass的方式实现（效率更高，因此这里采用FastClass）
        FastClass fastClass = FastClass.create(serviceBean.getClass());
        //通过索引的方式定位到具体的方法
        int methodIndex = fastClass.getIndex(request.getMethodName(), request.getParamTypes());
        //根据索引调用目标方法
        return fastClass.invoke(methodIndex,serviceBean, request.getParams());
    }
}
