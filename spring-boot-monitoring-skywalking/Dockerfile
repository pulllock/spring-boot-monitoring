FROM apache/skywalking-java-agent:8.12.0-java8

WORKDIR /app
COPY target/spring-boot-monitoring-skywalking-1.0.0-SNAPSHOT.jar spring-boot-monitoring-skywalking.jar
EXPOSE 8080
ENTRYPOINT ["java", \
            "-javaagent:/skywalking/agent/skywalking-agent.jar", \
            "-Dskywalking.agent.service_name=spring-boot-monitoring-skywalking", \
            "-Dskywalking.collector.backend_service=skywalking-oap-server:11800", \
            "-jar", "spring-boot-monitoring-skywalking.jar"]