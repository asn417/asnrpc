package com.asn.rpc.loadbalance;

import java.util.List;

public interface ServiceLoadBalance<T> {

    /**
     * 基于一致性哈希算法，在服务列表中选择一个合适的服务节点
     **/
    T select(List<T> servers,int hashCode);
}
