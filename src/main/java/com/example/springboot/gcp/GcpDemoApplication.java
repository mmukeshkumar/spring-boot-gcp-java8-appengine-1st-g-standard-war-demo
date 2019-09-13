package com.example.springboot.gcp;

import brave.SpanCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
//Based of
// 1) https://github.com/spring-cloud/spring-cloud-gcp/tree/master/spring-cloud-gcp-samples/spring-cloud-gcp-logging-sample
// 2) https://github.com/saturnism/spring-cloud-gcp/tree/master/spring-cloud-gcp-samples
//3) https://github.com/joshlong/bootiful-gcp
// 4) https://cloud.google.com/appengine/docs/standard/java11/config/appref
// 5) https://cloud.spring.io/spring-cloud-gcp/single/spring-cloud-gcp.html#_stackdriver_logging
//6)https://www.baeldung.com/spring-cloud-sleuth-single-application
//7) https://cloud.google.com/appengine/docs/standard/java/config/appref
//8) https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/async-config.html
//9)http://zetcode.com/springboot/springbootservletinitializer/

@Slf4j
public class GcpDemoApplication extends SpringBootServletInitializer implements WebMvcConfigurer  {

    static private final Logger logger = LoggerFactory.getLogger(GcpDemoApplication.class);

    @Autowired
    private Environment environment;


    public static void main(String[] args) {
        SpringApplication.run(GcpDemoApplication.class, args);

    }

    @Autowired
    private SpanCustomizer spanCustomizer;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(100);
        t.setQueueCapacity(50);
        t.setAllowCoreThreadTimeOut(true);
        t.setKeepAliveSeconds(120);
        t.initialize();
        configurer.setTaskExecutor(t);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
              /*  spanCustomizer.tag("session-id", request.getSession().getId());
                spanCustomizer.tag("environment", "DEV_GCP");*/
                // add customer_id or order_id or any other id which would help co-relate and track requests
                return true;
            }
        });
    }



}




