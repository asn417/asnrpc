package com.asn.rpc.core;

import lombok.Data;

import java.util.List;

/**
 * 描述服务的元信息
 **/
@Data
public class ServiceMeta {

    private String serviceName;
    private String version;
    private String serviceAddress;
    private String servicePort;

}
