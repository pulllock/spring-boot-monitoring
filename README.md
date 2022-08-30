# Spring-boot-monitoring

Spring boot项目监控，作为一个示例项目，研究其可行性。使用[Spring initializr](https://start.spring.io)生成一个可运行的简单Spring boot项目，开启Actuator和Jolokia支持，使用Telegraf采集项目运行信息，InfluxDB时序数据库存储数据，Grafana展示数据。

![](grafana/sample.png)

# 方案

- `Spring Boot Actuator` + `Telegraf` + `Influxdb` + `Grafana`
- `Spring Boot Actuator` + `Prometheus` + `Grafana`

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

# Spring Boot Actuator + Telegraf + Influxdb + Grafana方案

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

## 使用Docker启动Telegraf、Influxdb、Grafana以及示例项目

使用docker compose安装`Telegraf 2.x`、`Influxdb 2.x`和`Grafana`：

1. 先编写`docker-compose.yml`文件，内容参考下方的`docker-compose.yml`文件内容
2. 启动`docker compose up -d`
3. 访问grafana：`http://localhost:3000`
4. 在grafana创建数据源，在Query Language中选择Flux，这样才可以设置Influxdb 2.x的信息
5. 创建完数据源后，在Dashboards中选择Import，然后选择SpringBoot监控.json文件进行导入

## `docker-compose.yml`文件内容

grafana的默认用户名密码：`admin/admin`

```yaml

```

## 开始

下面说明怎样运行此项目并看到效果。首先克隆此项目：

```
git clone https://github.com/dachengxi/spring-boot-monitoring.git
```

### 准备

需要安装的列表

- influxdb
- telegraf
- grafana

### 安装

```
以上软件的安装步骤自行参考官方文档，这里不做重复。
```

### 配置

telegraf：

```
vi /etc/telegraf/telegraf.conf
```

打开或者添加以下配置

```
[[inputs.jolokia]]
    context = "/actuator/jolokia/read/"

[[inputs.jolokia.servers]]
     name = "spring-boot-monitoring-actuator-telegraf-influxdb-grafana"
     host = "127.0.0.1"
     port = "8080"

[[inputs.jolokia.metrics]]
     name = "heap_memory_usage"
     mbean  = "java.lang:type=Memory"
     attribute = "HeapMemoryUsage"

[[inputs.jolokia.metrics]]
     name = "thread_count"
     mbean  = "java.lang:type=Threading"
     attribute = "TotalStartedThreadCount,ThreadCount,DaemonThreadCount,PeakThreadCount"

[[inputs.jolokia.metrics]]
     name = "class_count"
     mbean  = "java.lang:type=ClassLoading"
     attribute = "LoadedClassCount,UnloadedClassCount,TotalLoadedClassCount"

[[inputs.jolokia.metrics]]
    name = "metrics"
    mbean  = "org.springframework.boot:name=metricsEndpoint,type=Endpoint"
    attribute = "Data"

[[inputs.jolokia.metrics]]
    name = "tomcat_max_threads"
    mbean  = "Tomcat:name=\"http-nio-8080\",type=ThreadPool"
    attribute = "maxThreads"

[[inputs.jolokia.metrics]]
    name = "tomcat_current_threads_busy"
    mbean  = "Tomcat:name=\"http-nio-8080\",type=ThreadPool"
    attribute = "currentThreadsBusy"
```

grafana：

运行grafana，访问http://localhost:3000 ，选择导入Dashboard，导入的文件是项目中的grafana/Spring Boot监控.json文件

### 运行

将Spring Boot项目导入到IDEA中，运行项目！

## 构建

* [Maven](https://maven.apache.org/) - 依赖管理

## 贡献

暂无

## 作者

* **ChengXi** - ** - [dachengxi](https://github.com/dachengxi)

## License

This project is licensed under the MIT License！
