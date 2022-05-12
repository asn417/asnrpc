package com.asn.rpc.consumer.consumer;

import com.asn.rpc.codec.RpcDecoder;
import com.asn.rpc.codec.RpcEncoder;
import com.asn.rpc.consumer.handler.RpcResponseHandler;
import com.asn.rpc.core.RegistryService;
import com.asn.rpc.core.RpcRequest;
import com.asn.rpc.core.RpcUtil;
import com.asn.rpc.core.ServiceMeta;
import com.asn.rpc.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcConsumer {

    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public RpcConsumer(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * protocol：封装了发送给服务端的消息
     * registryService：
     **/
    public void sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcUtil.buildServiceKey(request.getClassName(), request.getVersion());
        Object[] params = request.getParams();
        int hashCode = params != null ? params.hashCode():serviceKey.hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, hashCode);

        if (serviceMeta != null){
            ChannelFuture future = bootstrap.connect(serviceMeta.getServiceAddress(), serviceMeta.getServicePort()).sync();
            //通过监听器监听连接是否成功
            future.addListener((ChannelFutureListener) listener -> {
                if (future.isSuccess()){
                    log.info("connect remote service {} on port {} success",serviceMeta.getServiceAddress(),serviceMeta.getServicePort());
                }else {
                    log.error("connect remote service {} on port {} failed",serviceMeta.getServiceAddress(),serviceMeta.getServicePort());
                    eventLoopGroup.shutdownGracefully();
                }
            });
            //向远程服务发送请求
            future.channel().writeAndFlush(protocol);
        }
    }
}
