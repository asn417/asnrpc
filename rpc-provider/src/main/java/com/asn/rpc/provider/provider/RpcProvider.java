package com.asn.rpc.provider.provider;

import com.asn.rpc.codec.RpcDecoder;
import com.asn.rpc.codec.RpcEncoder;
import com.asn.rpc.core.RegistryService;
import com.asn.rpc.core.RpcUtil;
import com.asn.rpc.core.ServiceMeta;
import com.asn.rpc.provider.annotation.RpcService;
import com.asn.rpc.provider.cache.LocalCache;
import com.asn.rpc.provider.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private RegistryService registryService;
    private int servicePort;
    private String serverAddress;

    public RpcProvider(RegistryService registryService, int servicePort) {
        this.registryService = registryService;
        this.servicePort = servicePort;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //内部启动一个线程来发布服务
        new Thread(()->{
            try {
                startRpcServer();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startRpcServer() throws UnknownHostException {
        //获取当前服务器的地址
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();

        //采用netty基于tcp协议启动服务
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //构建服务启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加自定义的编解码器
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new RpcDecoder());
                        //添加自定义的业务逻辑处理器，处理客户端请求
                        pipeline.addLast(new RpcRequestHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        try {
            ChannelFuture future = serverBootstrap.bind(this.serverAddress, this.servicePort).sync();
            log.info("server start on {}:{}",this.serverAddress, this.servicePort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //扫描bean是否有@RpcService注解
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        //如果有，则构建服务元信息，并将其保存到注册中心
        if (rpcService != null){
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceAddress(serverAddress);
            serviceMeta.setServicePort(servicePort);
            serviceMeta.setServiceName(rpcService.serviceInterface().getName());
            serviceMeta.setVersion(rpcService.version());
            try {
                registryService.register(serviceMeta);
                //本地缓存一份
                LocalCache.rpcServiceCache.put(RpcUtil.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()),bean);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("register service {} failed",serviceMeta.getServiceName());
            }
        }
        return bean;
    }
}
