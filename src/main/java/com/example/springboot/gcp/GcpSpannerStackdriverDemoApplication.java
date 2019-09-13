package com.example.springboot.gcp;

import brave.SpanCustomizer;
import com.example.springboot.gcp.model.Order;
import com.example.springboot.gcp.web.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
//Based of
// 1) https://github.com/spring-cloud/spring-cloud-gcp/tree/master/spring-cloud-gcp-samples/spring-cloud-gcp-logging-sample
// 2) https://github.com/saturnism/spring-cloud-gcp/tree/master/spring-cloud-gcp-samples
//3) https://github.com/joshlong/bootiful-gcp
// 4) https://cloud.google.com/appengine/docs/standard/java11/config/appref
// 5) https://cloud.spring.io/spring-cloud-gcp/single/spring-cloud-gcp.html#_stackdriver_logging
//6)https://www.baeldung.com/spring-cloud-sleuth-single-application
public class GcpSpannerStackdriverDemoApplication implements WebMvcConfigurer {

    static private final Logger logger = LoggerFactory.getLogger(GcpSpannerStackdriverDemoApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(GcpSpannerStackdriverDemoApplication.class, args);
    }

    @Autowired
    private SpanCustomizer spanCustomizer;


    @Component
// This will run on application startup
// for setting up some data on startup
    class SpannerOrdersRunner implements ApplicationRunner {

        private final OrderService orderService;

        SpannerOrdersRunner(OrderService orderService) {
            this.orderService = orderService;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {


                orderService.deleteAllOrders();
                Order order1 = new Order();
                order1.setCustomerId(UUID.randomUUID().toString());
                order1.setFirstName("Mukesh");
                order1.setLastName("Kumar");

           /* SimpleDateFormat sdfAmerica = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
            sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
             String dateTime = sdfAmerica.format(new Date());
*/
                order1.setOrderDate(new Date());

                Order order2 = new Order();
                order2.setCustomerId(UUID.randomUUID().toString());
                order2.setFirstName("Zippy");
                order2.setLastName("Kumar");
                order2.setOrderDate(new Date());

                List<Order> orders = new ArrayList<>();
                orders.add(order1);
                orders.add(order2);

                String newOrderId = orderService.createOrder(order1, true);
                logger.info("Order created, order Id:  {}", newOrderId);
                newOrderId = orderService.createOrder(order2, true);
                logger.info("Order created, order Id:  {}", newOrderId);

        }
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                //spanCustomizer.tag("session-id", request.getSession().getId());
                //spanCustomizer.tag("environment", "DEV_GCP");
                // add customer_id or order_id or any other id which would help co-relate and track requests
                return true;
            }
        });
    }

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}




