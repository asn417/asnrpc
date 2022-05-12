package com.asn.rpc.consumer.handler;

import com.asn.rpc.consumer.consumer.RpcRequestHolder;
import com.asn.rpc.core.RpcFuture;
import com.asn.rpc.core.RpcResponse;
import com.asn.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        //接收到服务端响应结果后，将其设置到消息对应的future中
        long msgId = protocol.getHeader().getMsgId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.get(msgId);
        future.getPromise().setSuccess(protocol.getBody());
    }
}
