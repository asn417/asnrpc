package com.asn.rpc.core;

public class RpcUtil {

    /**
     * 根据服务的名称和版本构建服务的唯一标识
     **/
    public static String buildServiceKey(String serviceName,String version){
        StringBuilder builder = new StringBuilder(serviceName);
        builder.append("#");
        builder.append(version);
        return builder.toString();
    }
}
