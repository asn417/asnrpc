package com.asn.rpc.provider.service;

import com.asn.rpc.UserService;
import com.asn.rpc.provider.annotation.RpcService;

@RpcService(serviceInterface = UserService.class,version = "1.0.0")
public class UserServiceImpl implements UserService {

    @Override
    public String hello() {
        return "hello rpc";
    }
}
