FROM openjdk:8-jre-alpine
WORKDIR /app

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
apk update

COPY target/spring-boot-monitoring-sleuth-zipkin-1.0.0-SNAPSHOT.jar spring-boot-monitoring-sleuth-zipkin.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-boot-monitoring-sleuth-zipkin.jar"]