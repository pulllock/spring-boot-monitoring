# Spring-boot-monitoring-skywalking

## 将当前项目打包为Docker镜像

1. 在`spring-boot-monitoring-skywalking`根目录下创建`Dockerfile`文件，文件内容如下：

   ```
   FROM apache/skywalking-java-agent:8.12.0-java8

   WORKDIR /app
   COPY target/spring-boot-monitoring-skywalking-1.0.0-SNAPSHOT.jar spring-boot-monitoring-skywalking.jar
   EXPOSE 8080
   ENTRYPOINT ["java", \
   "-javaagent:/skywalking/agent/skywalking-agent.jar", \
   "-Dskywalking.agent.service_name=spring-boot-monitoring-skywalking", \
   "-Dskywalking.collector.backend_service=skywalking-oap-server:11800", \
   "-jar", "spring-boot-monitoring-skywalking.jar"]
   ```

2. 将项目`spring-boot-monitoring-skywalking`进行打包：`mvn clean package -Dmaven.test.skip=true`

3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-skywalking:1.0.0-SNAPSHOT .`

## 使用Docker启动skywalking server、skywalking ui以及示例项目

使用docker compose安装：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问：`http://localhost:18080`

## 怎样运行当前示例

1. 克隆当前项目，进入`spring-boot-monitoring-skywalking`目录下
2. 将项目`spring-boot-monitoring-skywalking`进行打包：`mvn clean package -Dmaven.test.skip=true`
3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-skywalking:1.0.0-SNAPSHOT .`
4. 启动：`docker compose up -d`
5. 使用skywalking，访问：`http://localhost:18080`

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

   # skywalking-oap-server服务
   skywalking-oap-server:
      image: apache/skywalking-oap-server:8.9.1
      ports:
         - 11800:11800
         - 12800:12800
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
         - SW_STORAGE=elasticsearch
         - SW_STORAGE_ES_CLUSTER_NODES=es7:9200

   # skywalking-ui服务
   skywalking-ui:
      image: apache/skywalking-ui:8.9.1
      ports:
         - 18080:8080
      networks:
         - local_net
      depends_on:
         - skywalking-oap-server
      environment:
         - SW_OAP_ADDRESS=http://skywalking-oap-server:12800

   # 示例项目服务
   spring-boot-monitoring-skywalking:
      image: local_test/spring-boot-monitoring-skywalking:1.0.0-SNAPSHOT
      ports:
         - 8080:8080
      networks:
         - local_net
      depends_on:
         skywalking-oap-server:
            condition: service_healthy
```