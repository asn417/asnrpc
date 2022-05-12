package com.asn.rpc.provider.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {
    public static final Map<String,Object> rpcServiceCache = new ConcurrentHashMap<>();
}
