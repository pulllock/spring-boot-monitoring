# Spring-boot-monitoring-sleuth-zipkin

## 将当前项目打包为Docker镜像

1. 在`spring-boot-monitoring-sleuth-zipkin`根目录下创建`Dockerfile`文件，文件内容如下：

   ```
   FROM openjdk:8-jre-alpine
   WORKDIR /app
   
   RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
   apk update && \
   COPY target/spring-boot-monitoring-sleuth-zipkin-1.0.0-SNAPSHOT.jar spring-boot-monitoring-sleuth-zipkin.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "spring-boot-monitoring-sleuth-zipkin.jar"]
   ```

2. 将项目`spring-boot-monitoring-sleuth-zipkin`进行打包：`mvn clean package -Dmaven.test.skip=true`

3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-sleuth-zipkin:1.0.0-SNAPSHOT .`

## 使用Docker启动es、zipkin以及示例项目

使用docker compose安装：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问：`http://localhost:9411`

## 怎样运行当前示例

1. 克隆当前项目，进入`spring-boot-monitoring-sleuth-zipkin`目录下
2. 将项目`spring-boot-monitoring-sleuth-zipkin`进行打包：`mvn clean package -Dmaven.test.skip=true`
3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-sleuth-zipkin:1.0.0-SNAPSHOT .`
4. 启动：`docker compose up -d`
5. 使用zipkin，访问：`http://localhost:9411`

## `docker-compose.yml`文件内容

```yaml
version: "3.8"

# 定义网络：local_net
networks:
   local_net:
      name: local_net

# 定义服务
services:
   # es7服务
   es7:
      image: elasticsearch:7.17.5
      ports:
         - 9200:9200
         - 9300:9300
      networks:
         - local_net
      healthcheck:
         test: [ "CMD-SHELL", "curl --silent --fail localhost:9200/_cluster/health || exit 1" ]
         interval: 30s
         timeout: 10s
         retries: 3
         start_period: 10s
      environment:
         - discovery.type=single-node

   # zipkin服务
   zipkin:
      image: openzipkin/zipkin:2.23.18
      ports:
         - 9411:9411
      networks:
         - local_net
      depends_on:
         es7:
            condition: service_healthy
      healthcheck:
         test: [ "CMD-SHELL", "/skywalking/bin/swctl ch" ]
         interval: 30s
         timeout: 10s
         retries: 3
         start_period: 10s
      environment:
         - STORAGE_TYPE=elasticsearch
         - ES_HOSTS=es7:9200

   # 示例项目服务
   spring-boot-monitoring-sleuth-zipkin:
      image: local_test/spring-boot-monitoring-sleuth-zipkin:1.0.0-SNAPSHOT
      ports:
         - 8080:8080
      networks:
         - local_net
      depends_on:
         zipkin:
            condition: service_healthy
```