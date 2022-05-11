package com.asn.rpc.core;

public interface RegistryService {

    public void register(ServiceMeta serviceMeta) throws Exception;
    public void unregister(ServiceMeta serviceMeta) throws Exception;
    public ServiceMeta discovery(String serviceKey,int hashCode) throws Exception;
}
