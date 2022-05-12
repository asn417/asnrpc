package com.asn.rpc.consumer.controller;

import com.asn.rpc.UserService;
import com.asn.rpc.consumer.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RpcReference(registryAddress = "asn417-01:2181",timeout = 3000)
    private UserService userService;

    @GetMapping("/test")
    public void test(){
        System.out.println(userService.hello());
    }
}
