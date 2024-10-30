package com.example.demo;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        var ac = (ApplicationContext) SpringApplication.run(DemoApplication.class, args);
        var customerService = ac.getBean(CustomerService.class);
        customerService.doSomething();
    }

    @Bean
    static MyLoggingBeanPostProcessor myBeanPostProcessor() {
        return new MyLoggingBeanPostProcessor();
    }

    @Bean
    static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
        return new MyBeanFactoryPostProcessor();
    }
}

class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (var beanDefinitionName : beanDefinitionNames) {
            System.out.println("-------------------------------------------------");
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            System.out.println(beanDefinition);
        }
    }
}

class MyLoggingBeanPostProcessor implements BeanPostProcessor {

    private Object createLoggingProxy(Object original) {
        var pfb = new ProxyFactoryBean();
        pfb.setTarget(original);
        pfb.setProxyTargetClass(true);
        pfb.addAdvice((MethodInterceptor) invocation -> {
            try {
                System.out.println("before [" + invocation.getMethod().getName() + "]");
                return invocation.proceed();
            } finally {
                System.out.println("after [" + invocation.getMethod().getName() +
                        "]");
            }
        });
        return pfb.getObject();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization: " + beanName + ":[" + bean + "]");

        if (bean.getClass().getAnnotationsByType(Logged.class).length > 0) {
            System.out.println("we need to log this " + beanName + "bean!");
            return createLoggingProxy(bean);
        }

        return bean;
    }
}



@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Logged {
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@interface TeoBean {

    @AliasFor(annotation = Component.class)
    String value() default "";
}

//@Scope ( "thread")
@Logged
@TeoBean
class CustomerService {


    void doSomething() {
        System.out.println("do something");
    }
}

//@ComponentScan(basePackages = "com.example.demo")
//@PropertySource({"classpath:application.properties"})



/*
class Config {

    CustomerService customerService(DataSource db) {
        return new CustomerService(db);
    }

    DataSource dataSource(String username, String password) {
        System.out.println("username: " + username + " password: " + password + "");
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }


}*/

/*

class MyDbFactoryBean implements FactoryBean<DataSource> {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public DataSource getObject() throws Exception {
        System.out.println("username: " + username + " password: " + password + "");
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();

    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }
}
*/

//@ComponentScan
//@EnableAutoConfiguration
//@Configuration


//
//    @Bean
//    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

/*    @Bean
    DataSource dataSource(*//*@Value("${spring.datasource.username}") String username,
                          @Value("${spring.datasource.password}") String password*//*) {
//        System.out.println("username: " + username + " password: " + password + "");
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }*/



//        var cpXmlAc = (ApplicationContext) new ClassPathXmlApplicationContext("/dontdothis.xml");
//        var annotionConfigAc = (ApplicationContext) new AnnotationConfigApplicationContext(MyJavaConfig.class);
        