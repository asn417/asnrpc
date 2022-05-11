package com.asn.rpc.factory;

import com.asn.rpc.core.RegistryService;
import com.asn.rpc.enums.RegistryTypeEnum;
import com.asn.rpc.impl.ZookeeperRegistryServiceImpl;

public class RegistryServiceFactory {
    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddress, RegistryTypeEnum type) throws Exception {
        if (registryService == null){
            synchronized (RegistryServiceFactory.class){
                if (registryService == null){
                    switch (type){
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryServiceImpl(registryAddress);
                            break;
                        default:
                            throw new IllegalArgumentException("registry type is illegal "+type);
                    }
                }
            }
        }
        return registryService;
    }
}
