package com.asn.rpc.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "asnrpc")
public class RpcProperties {

    private String registryType;
    private String registryAddress;
    private int servicePort;
}
