FROM openjdk:8-jre-alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
apk update && \
mkdir -p /app
COPY target/spring-boot-monitoring-actuator-telegraf-influxdb-grafana-1.0.0-SNAPSHOT.jar app/spring-boot-monitoring-actuator-telegraf-influxdb-grafana.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app/spring-boot-monitoring-actuator-telegraf-influxdb-grafana.jar"]