package com.asn.rpc.consumer.consumer;

import com.asn.rpc.core.RegistryService;
import com.asn.rpc.enums.RegistryTypeEnum;
import com.asn.rpc.factory.RegistryServiceFactory;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class RpcReferenceBean implements FactoryBean<Object> {
    @Setter
    private Class<?> interfaceClass;
    @Setter
    private String registryType;
    @Setter
    private String registryAddress;
    @Setter
    private String version;
    @Setter
    private long timeout;
    private Object object;

    @Override
    public Object getObject() throws Exception {
        //返回实例化后的具体bean
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        //返回bean的类型
        return interfaceClass;
    }

    public void init() throws Exception{
        //完成object对象的实例化，生成一个动态代理对象

        //获取注册中心的实例
        RegistryService registryService = RegistryServiceFactory.getInstance(registryAddress, RegistryTypeEnum.valueOf(registryType));
        //构建代理对象
        this.object = Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},new RpcInvokerProxy(registryService,version,timeout));
    }
}
