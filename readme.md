# Description:
* Demo Java 8 maven project showcasing Spring boot, Spring cloud GCP Spanner, Pub/Sub and Stackdriver tracing and logging.
* Its deployable to gae-standard 1st generation which uses Java8 and Jetty 9
* It demos distributed tracing, co-relating logs in cases where one request spawns multiple requests across multiple micro services
* It also demos integration with GCP Spanner DB using Spring cloud GCP

# Prerequisites 
1. Install Java 8 JDK
2. Set up a project in GCP through https://cloud.google.com/ or gcloud CLI
3. Enable billing

# Building
mvnw clean install

# Running locally: 

1. Change spring active profile in appengine-web.xml file to dev_local
2. Copy spring-boot-gcp-demo-251616-80aae1c91d25.json to a local directory and update the path
under application-dev_local.properties file
3. Run using the following commands:
set JAVA_HOME=C:\dev\Java\jdk1.8.0_211
Set PATH=%JAVA_HOME%\bin;%PATH%
set JAVA_OPTS=-Xms2g -Xmx2g
cd C:\dev\projects\gcp\spring-boot-gcp-java8-appengine-1st-g-standard-war-demo
mvnw clean install appengine:run

### NOTE: You can also run by Importing project into Intellij IDE and running the GcpDemoApplication class as a Java application

# Testing locally:
* List all available REST endpoints
 * curl -i http://localhost:8080
* List orders
  * curl -i http://localhost:8080/orders

# Deploying to google app engine:
./mvnw appengine:deploy

# sample curl requests
curl -i -XPOST \
-H "Content-Type: application/json" \
-d '{"firstName":"Larry","lastName":"Grooves","customerId":"53a2e699-0205-4546-aae6-8fc903c478c7","orderDate":"2019-10-10T18:06:48.526+0000"}' \
http://localhost:8080/orders

# submit request and get trace Id back
curl -i -XPOST \
-H "Content-Type: application/json" \
-d '{"firstName":"Larry","lastName":"Grooves","customerId":"53a2e699-0205-4546-aae6-8fc903c478c7","orderDate":"2019-10-10T18:06:48.526+0000"}' \
https://spring-boot-gcp-demo-251616.appspot.com/orders

HTTP/2 201
set-cookie: JSESSIONID=node0piy6x6ldeidw14trhe11h2axe3.node0; Path=/
expires: Thu, 01 Jan 1970 00:00:00 GMT
content-type: text/plain;charset=utf-8
x-cloud-trace-context: 46dc8f6388fadba0d182970fbf88d792;o=1
date: Tue, 10 Sep 2019 20:38:42 GMT
server: Google Frontend
content-length: 36
alt-svc: quic=":443"; ma=2592000; v="46,43,39"

9b2c6a02-119d-4f1a-b781-de6ccad32689

Advanced search entry in stackdriver logviewer
(trace="projects/spring-boot-gcp-demo-251616/traces/46dc8f6388fadba0d182970fbf88d792")

# force a trace Id:
curl -i -XPOST \
-H "Content-Type: application/json" \
-H "X-Cloud-Trace-Context: 105445aa7843bc8bf206b120001000" \
-d '{"firstName":"My","lastName":"Nyugen","customerId":"53a2e698-0205-4546-aae6-8fc903c478c7","orderDate":"2019-10-10T18:06:48.526+0000"}' \
https://spring-boot-gcp-demo-251616.appspot.com/orders
##  Note: This wont work for some reason, the passed in X-Cloud-Trace-Context does not get used


# Issues/Todos
 * force a trace Id wont work, the passed in X-Cloud-Trace-Context does not get used, instead a new X-Cloud-Trace-Context gets used and returned
 * when deployed to app engine standard, its takes upto 30 seconds to start an instances since all instances get shutdown when no requests come in
   for certain period of time
      
 # Please ignore this exception while on startup, it does not affect application
 
 2019-09-12 14:17:35.006 ERROR [orders-service,,,] 660 --- [           main] i.g.i.ManagedChannelOrphanWrapper        : *~*~*~ Channel ManagedChannelImpl{logId=1, target=logging.googleapis.com:443} was not shutdown properly!!! ~*~*~*
     Make sure to call shutdown()/shutdownNow() and wait until awaitTermination() returns true.
 
 java.lang.RuntimeException: ManagedChannel allocation site
 	at io.grpc.internal.ManagedChannelOrphanWrapper$ManagedChannelReference.<init>(ManagedChannelOrphanWrapper.java:94)
 	at io.grpc.internal.ManagedChannelOrphanWrapper.<init>(ManagedChannelOrphanWrapper.java:52)
 	at io.grpc.internal.ManagedChannelOrphanWrapper.<init>(ManagedChannelOrphanWrapper.java:43)
 	at io.grpc.internal.AbstractManagedChannelImplBuilder.build(AbstractManagedChannelImplBuilder.java:514)
 	at com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.createSingleChannel(InstantiatingGrpcChannelProvider.java:223)
 	at com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.createChannel(InstantiatingGrpcChannelProvider.java:164)
 	at com.google.api.gax.grpc.InstantiatingGrpcChannelProvider.getTransportChannel(InstantiatingGrpcChannelProvider.java:156)
 	at com.google.api.gax.rpc.ClientContext.create(ClientContext.java:157)
 	at com.google.api.gax.rpc.ClientContext.create(ClientContext.java:122)
 	at com.google.cloud.logging.spi.v2.GrpcLoggingRpc.<init>(GrpcLoggingRpc.java:132)
 	at com.google.cloud.logging.LoggingOptions$DefaultLoggingRpcFactory.create(LoggingOptions.java:61)
 	at com.google.cloud.logging.LoggingOptions$DefaultLoggingRpcFactory.create(LoggingOptions.java:55)
 	at com.google.cloud.ServiceOptions.getRpc(ServiceOptions.java:510)
 	at com.google.cloud.logging.LoggingOptions.getLoggingRpcV2(LoggingOptions.java:129)
 	at com.google.cloud.logging.LoggingImpl.<init>(LoggingImpl.java:109)
 	at com.google.cloud.logging.LoggingOptions$DefaultLoggingFactory.create(LoggingOptions.java:46)
 	at com.google.cloud.logging.LoggingOptions$DefaultLoggingFactory.create(LoggingOptions.java:41)
 	at com.google.cloud.ServiceOptions.getService(ServiceOptions.java:498)









