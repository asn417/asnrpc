package com.asn.rpc.consumer.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String registryType() default "ZOOKEEPER";

    String registryAddress() default "asn417-01:2181";

    String version() default "1.0.0";

    long timeout() default 3000;
}
