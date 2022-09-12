# Spring-boot-monitoring

Spring boot项目监控，作为一个示例项目，研究其可行性。

![](grafana/sample.png)

# 方案

- `Spring Boot Actuator` + `Telegraf` + `Influxdb 1.x` + `Grafana`
- `Spring Boot Actuator` + `Telegraf` + `Influxdb 2.x` + `Grafana`
- `Spring Boot Actuator` + `Prometheus` + `Grafana`

# Spring Boot Actuator + Telegraf + Influxdb 1.x + Grafana方案

Spring boot项目，开启Actuator和Jolokia支持，使用Telegraf采集项目运行信息，InfluxDB 1.x时序数据库存储数据，Grafana展示数据。

## 将当前项目打包为Docker镜像

1. 在`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana`根目录下创建`Dockerfile`文件，文件内容如下：

   ```
   FROM openjdk:8-jre-alpine
   
   RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
   apk update && \
   mkdir -p /app
   COPY target/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana-1.0.0-SNAPSHOT.jar app/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana.jar"]
   ```

2. 将项目`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`

3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana:1.0.0-SNAPSHOT .`

## 使用Docker启动Telegraf、Influxdb 1.x、Grafana以及示例项目

使用docker compose安装`Telegraf`、`Influxdb 1.x`和`Grafana`：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
4. 在grafana创建数据源，在Query Language中选择InfluxQL，这样才可以设置Influxdb 1.x的信息
5. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana/grafana/SpringBoot监控.json`文件进行导入

## 怎样运行当前方案

1. 克隆当前项目，进入`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana`目录下
2. 将项目`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`
3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana:1.0.0-SNAPSHOT .`
4. 启动：`docker compose up -d`
5. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
6. 在grafana创建数据源，在Query Language中选择InfluxQL，这样才可以设置Influxdb 1.x的信息
7. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana/grafana/SpringBoot监控.json`文件进行导入

## `docker-compose.yml`文件内容

```yaml
version: "3.8"

# 定义网络：local_net
networks:
   local_net:
      name: local_net

# 定义服务
services:
   # influxdb服务
   influxdb:
      image: influxdb:1.8
      ports:
         - 8086:8086
      networks:
         - local_net
      environment:
         - INFLUXDB_DB=telegraf
         - INFLUXDB_ADMIN_USER=admin
         - INFLUXDB_ADMIN_PASSWORD=12345678
         - INFLUXDB_USER=telegraf
         - INFLUXDB_USER_PASSWORD=12345678

   # telegraf服务
   telegraf:
      image: telegraf:latest
      networks:
         - local_net
      depends_on:
         - influxdb
      volumes:
         - ./telegraf/telegraf.conf:/etc/telegraf/telegraf.conf:ro

   # grafana服务
   grafana:
      image: grafana/grafana:latest
      ports:
         - 3000:3000
      networks:
         - local_net

   # 示例项目服务
   spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana:
      image: local_test/spring-boot-monitoring-actuator-telegraf-influxdb1x-grafana:1.0.0-SNAPSHOT
      ports:
         - 8080:8080
      networks:
         - local_net
```

# Spring Boot Actuator + Telegraf + Influxdb 2.x + Grafana方案

Spring boot项目，开启Actuator和Jolokia支持，使用Telegraf采集项目运行信息，InfluxDB 2.x时序数据库存储数据，Grafana展示数据。

## 将当前项目打包为Docker镜像

1. 在`spring-boot-monitoring-actuator-telegraf-influxdb-grafana`根目录下创建`Dockerfile`文件，文件内容如下：
   
   ```
   FROM openjdk:8-jre-alpine
   
   RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
   apk update && \
   mkdir -p /app
   COPY target/spring-boot-monitoring-actuator-telegraf-influxdb-grafana-1.0.0-SNAPSHOT.jar app/spring-boot-monitoring-actuator-telegraf-influxdb-grafana.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app/spring-boot-monitoring-actuator-telegraf-influxdb-grafana.jar"]
   ```

2. 将项目`spring-boot-monitoring-actuator-telegraf-influxdb-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`

3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-telegraf-influxdb-grafana:1.0.0-SNAPSHOT .`

## 使用Docker启动Telegraf、Influxdb 2.x、Grafana以及示例项目

使用docker compose安装`Telegraf`、`Influxdb 2.x`和`Grafana`：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
4. 在grafana创建数据源，在Query Language中选择Flux，这样才可以设置Influxdb 2.x的信息
5. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-telegraf-influxdb-grafana/grafana/SpringBoot监控.json`文件进行导入

## 怎样运行当前方案

1. 克隆当前项目，进入`spring-boot-monitoring-actuator-telegraf-influxdb-grafana`目录下
2. 将项目`spring-boot-monitoring-actuator-telegraf-influxdb-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`
3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-telegraf-influxdb-grafana:1.0.0-SNAPSHOT .`
4. 启动：`docker compose up -d`
5. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
6. 在grafana创建数据源，在Query Language中选择Flux，这样才可以设置Influxdb 2.x的信息
7. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-telegraf-influxdb-grafana/grafana/SpringBoot监控.json`文件进行导入

## `docker-compose.yml`文件内容

```yaml
version: "3.8"

# 定义网络：local_net
networks:
  local_net:
    name: local_net

# 定义服务
services:
  # influxdb服务
  influxdb:
    image: influxdb:latest
    ports:
      - 8086:8086
    networks:
      - local_net
    environment:
      - DOCKER_INFLUXDB_INIT_MODE=setup
      - DOCKER_INFLUXDB_INIT_USERNAME=admin
      - DOCKER_INFLUXDB_INIT_PASSWORD=12345678
      - DOCKER_INFLUXDB_INIT_ORG=cxis
      - DOCKER_INFLUXDB_INIT_BUCKET=cxis-bucket
      - DOCKER_INFLUXDB_INIT_ADMIN_TOKEN=12345678

  # telegraf服务
  telegraf:
    image: telegraf:latest
    networks:
      - local_net
    depends_on:
      - influxdb
    volumes:
      - ./telegraf/telegraf.conf:/etc/telegraf/telegraf.conf:ro

  # grafana服务
  grafana:
    image: grafana/grafana:latest
    ports:
      - 3000:3000
    networks:
      - local_net

  # 示例项目服务
  spring-boot-monitoring-actuator-telegraf-influxdb-grafana:
    image: local_test/spring-boot-monitoring-actuator-telegraf-influxdb-grafana:1.0.0-SNAPSHOT
    ports:
        - 8080:8080
    networks:
      - local_net
```

# Spring Boot Actuator + Prometheus + Grafana方案

Spring boot项目，开启Actuator支持，使用Prometheus采集项目运行信息并存储数据，Grafana展示数据。

## 将当前项目打包为Docker镜像

1. 在`spring-boot-monitoring-actuator-prometheus-grafana`根目录下创建`Dockerfile`文件，文件内容如下：

   ```
   FROM openjdk:8-jre-alpine
   
   RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
   apk update && \
   mkdir -p /app
   COPY target/spring-boot-monitoring-actuator-prometheus-grafana-1.0.0-SNAPSHOT.jar app/spring-boot-monitoring-actuator-prometheus-grafana.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app/spring-boot-monitoring-actuator-prometheus-grafana.jar"]
   ```

2. 将项目`spring-boot-monitoring-actuator-prometheus-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`

3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-prometheus-grafana:1.0.0-SNAPSHOT .`

## 使用Docker启动Prometheus、Grafana以及示例项目

使用docker compose安装`Prometheus`和`Grafana`：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问prometheus：`http://localhost:9090`
3. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
4. 在grafana创建数据源，选择Prometheus后填写信息即可
5. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-prometheus-grafana/grafana/SpringBoot监控.json`文件进行导入

## 怎样运行当前方案

1. 克隆当前项目，进入`spring-boot-monitoring-actuator-prometheus-grafana`目录下
2. 将项目`spring-boot-monitoring-actuator-prometheus-grafana`进行打包：`mvn clean package -Dmaven.test.skip=true`
3. 创建应用的镜像，执行命令：`docker build -t local_test/spring-boot-monitoring-actuator-prometheus-grafana:1.0.0-SNAPSHOT .`
4. 启动：`docker compose up -d`
5. 访问prometheus：`http://localhost:9090`
5. 访问grafana：`http://localhost:3000`，grafana的默认用户名密码：`admin/admin`
6. 在grafana创建数据源，选择Prometheus后填写信息即可
7. 创建完数据源后，在Dashboards中选择Import，然后选择`spring-boot-monitoring-actuator-prometheus-grafana/grafana/SpringBoot监控.json`文件进行导入

## `docker-compose.yml`文件内容

```yaml
version: "3.8"

# 定义网络：local_net
networks:
   local_net:
      name: local_net

# 定义服务
services:
   # prometheus服务
   prometheus:
      image: prom/prometheus:latest
      ports:
         - 9090:9090
      networks:
         - local_net
      volumes:
         - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml

   # grafana服务
   grafana:
      image: grafana/grafana:latest
      ports:
         - 3000:3000
      networks:
         - local_net

   # 示例项目服务
   spring-boot-monitoring-actuator-prometheus-grafana:
      image: local_test/spring-boot-monitoring-actuator-prometheus-grafana:1.0.0-SNAPSHOT
      ports:
         - 8080:8080
      networks:
         - local_net
```

# Spring Boot Actuator介绍

## 可用的endpoints

| ID                 | 描述                                                                                      | JXM默认暴露 | Web默认暴露 |
| ------------------ | --------------------------------------------------------------------------------------- | ------- | ------- |
| `auditevents`      | 暴露审计事件，比如登录相关的事件信息等等，需要容器中存在`AuditEventRepository`类型的` Bean`                            | Yes     | No      |
| `beans`            | 显示所有的Spring的`Bean`                                                                      | Yes     | No      |
| `caches`           | 暴露可用的缓存                                                                                 | Yes     | No      |
| `conditions`       | 显示在配置类和自动配置类上的评估条件，并且显示他们匹配或者不匹配的原因                                                     | Yes     | No      |
| `configprops`      | 显示所有`@ConfigurationProperties`列表                                                        | Yes     | No      |
| `env`              | 暴露`ConfigurableEnvironment`中的环境属性，包括：配置文件中的内容、系统变量等                                     | Yes     | No      |
| `flyway`           | 显示Flyway数据库迁移，需要容器中存在`Flyway`类型的`Bean`                                                  | Yes     | No      |
| `health`           | 显示应用健康信息、运行状态                                                                           | Yes     | Yes     |
| `httptrace`        | 显示HTTP跟踪信息（默认情况下显示最近的100条信息），需要容器中存在`HttpTraceRepository`类型的`Bean`                      | Yes     | No      |
| `info`             | 显示配置文件中`info`开头的配置信息                                                                    | Yes     | No      |
| `integrationgraph` | 显示`Spring Integration`图，需要有`spring-integration-core`的依赖                                 | Yes     | No      |
| `loggers`          | 显示和修改日志的配置信息                                                                            | Yes     | No      |
| `liquibase`        | 显示`Liquibase`数据库迁移，需要容器中存在`Liquibase`类型的`Bean`                                          | Yes     | No      |
| `metrics`          | 显示引用的指标信息，比如：内存、线程、垃圾回收、数据库连接池等                                                         | Yes     | No      |
| `mappings`         | 显示`@RequestMapping`的所有URI路径列表                                                           | Yes     | No      |
| `quartz`           | 显示`Quartz Scheduler`的任务                                                                 | Yes     | No      |
| `scheduledtasks`   | 显示应用中的定时任务信息                                                                            | Yes     | No      |
| `sessions`         | 允许获取或删除用户的`session`，需要是基于`Spring Session`存储并且是基于`Servlet`的web应用                         | Yes     | No      |
| `shutdown`         | 可以优雅关闭应用，默认禁用                                                                           | Yes     | No      |
| `startup`          | 显示`ApplicationStartup`收集的有关启动的数据，需要将`SpringApplication`配置为`BufferingApplicationStartup` | Yes     | No      |
| `threaddump`       | 执行线程转储                                                                                  | Yes     | No      |

### web应用专用的endpoints

| ID           | 描述                                                                                       | JMX默认暴露 | HTTP默认暴露 |
| ------------ | ---------------------------------------------------------------------------------------- | ------- | -------- |
| `heapdump`   | 返回一个堆转储文件                                                                                | N/A     | No       |
| `jolokia`    | 将JMX通过HTTP暴露出去，需要添加依赖`jolokia-core`                                                      | N/A     | No       |
| `logfile`    | 如果`logging.file.name`或者`logging.file.path`设置了，则可以获取到日志文件内容，支持使用Http的请求头的`Range`来部分获取日志内容 | N/A     | No       |
| `prometheus` | 支持将指标暴露为`Prometheus`可识别的格式，需要添加依赖`micrometer-registry-prometheus`                        | N/A     | No       |