package com.asn.rpc.loadbalance.impl;

import com.asn.rpc.core.ServiceMeta;
import com.asn.rpc.loadbalance.ServiceLoadBalance;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ZookeeperLoadBalanceImpl implements ServiceLoadBalance<ServiceInstance<ServiceMeta>> {

    private static final int VIRTUAL_NODE_SIZE = 2;

    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode) {
        //1.根据一致性哈希算法将服务列表构建成环
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = this.buildHashRing(servers);
        //2.根据客户端的hashCode获取对应的服务实例
        return this.assignServerInstance(ring,hashCode);
    }

    private ServiceInstance<ServiceMeta> assignServerInstance(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode) {
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> instanceEntry = ring.ceilingEntry(hashCode);
        if (instanceEntry == null){
            instanceEntry = ring.firstEntry();
        }
        return instanceEntry.getValue();
    }

    private TreeMap<Integer, ServiceInstance<ServiceMeta>> buildHashRing(List<ServiceInstance<ServiceMeta>> servers) {
        TreeMap<Integer,ServiceInstance<ServiceMeta>> ring = new TreeMap<>();

        //每个服务节点构建对应的虚拟节点，并保存到环中
        for (ServiceInstance<ServiceMeta> server : servers) {
            String instanceKey = buildServerInstanceKey(server);
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((instanceKey+i).hashCode(),server);
            }
        }
        return ring;
    }

    private String buildServerInstanceKey(ServiceInstance<ServiceMeta> server){
        ServiceMeta serviceMeta = server.getPayload();
        StringBuilder stringBuilder = new StringBuilder(serviceMeta.getServiceAddress());
        stringBuilder.append(":");
        stringBuilder.append(serviceMeta.getServicePort());
        String serverKey = stringBuilder.toString();
        return serverKey;
    }
}
