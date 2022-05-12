package com.asn.rpc.provider.config;

import com.asn.rpc.core.RegistryService;
import com.asn.rpc.enums.RegistryTypeEnum;
import com.asn.rpc.factory.RegistryServiceFactory;
import com.asn.rpc.provider.provider.RpcProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider initRpcProvider() throws Exception {
        //获取配置的注册中心类型
        RegistryTypeEnum registryTypeEnum = RegistryTypeEnum.valueOf(rpcProperties.getRegistryType());
        //获取注册中心的地址
        String registryAddress = rpcProperties.getRegistryAddress();
        //根据注册中心的类型及地址，获取操作注册中心的客户端实例
        RegistryService registryService = RegistryServiceFactory.getInstance(registryAddress, registryTypeEnum);
        //
        return new RpcProvider(registryService,rpcProperties.getServicePort());

    }
}
