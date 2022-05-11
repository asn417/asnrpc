package com.asn.rpc.impl;

import com.asn.rpc.core.RegistryService;
import com.asn.rpc.core.RpcUtil;
import com.asn.rpc.core.ServiceMeta;
import com.asn.rpc.loadbalance.impl.ZookeeperLoadBalanceImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;

public class ZookeeperRegistryServiceImpl implements RegistryService {

    private static final String ZK_BASE_PATH = "asnrpc";
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    public ZookeeperRegistryServiceImpl(String registryAddress) throws Exception{
        //创建一个zk连接，并设置重试策略
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddress,new ExponentialBackoffRetry(1000,3));
        client.start();
        //初始化服务发现对象实例
        //创建一个序列化对象
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtil.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtil.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discovery(String serviceKey, int hashCode) throws Exception {
        //根据serviceKey获取服务列表
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);
        //根据负载均衡算法获取一个服务实例
        ServiceInstance<ServiceMeta> serviceInstance = new ZookeeperLoadBalanceImpl().select((List<ServiceInstance<ServiceMeta>>) serviceInstances, hashCode);
        if (serviceInstance != null){
            return serviceInstance.getPayload();
        }
        return null;
    }
}
