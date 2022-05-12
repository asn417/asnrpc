package com.asn.rpc.consumer.consumer;

import com.asn.rpc.consumer.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RpcConsumerPostProcessor implements BeanClassLoaderAware, ApplicationContextAware, BeanFactoryPostProcessor {

    private ClassLoader classLoader;

    private ApplicationContext applicationContext;

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //在bean初始化之前的操作

        //从beanFactory获取所有的bean定义
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null){
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                //判断类对象是否有@RpcReference注解
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        parseRpcReference(field);
                    }
                });
            }
        }
        //bean解析之后，将其注册到容器中
        System.out.println(applicationContext.containsBean("userService"));
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) configurableListableBeanFactory;
        beanDefinitionMap.forEach((beanName,beanDef)->{
            if (applicationContext.containsBean(beanName)){
                throw new IllegalArgumentException("Spring context already has bean "+beanName);
            }
            registry.registerBeanDefinition(beanName,beanDef);
        });
        System.out.println(applicationContext.containsBean("userService"));
    }

    private void parseRpcReference(Field field) {
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);
        if (annotation != null){
            //基于RpcReferenceBean完成bean的实例化
            BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            //设置初始化方法
            definitionBuilder.setInitMethodName("init");
            //为其成员变量赋值
            definitionBuilder.addPropertyValue("interfaceClass",field.getType());
            definitionBuilder.addPropertyValue("registryType",annotation.registryType());
            definitionBuilder.addPropertyValue("registryAddress",annotation.registryAddress());
            definitionBuilder.addPropertyValue("version",annotation.version());
            definitionBuilder.addPropertyValue("timeout",annotation.timeout());
            //基于这些属性创建beanDefinition
            BeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();

            beanDefinitionMap.put(field.getName(), beanDefinition);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
