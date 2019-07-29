# spring-boot-vue-admin
spring boot + vue + shiro + vue-cli + mybatis 后台管理系统独自尝试，后续会不断完善的

### spring boot

1. `spring-boot-starter-web` 内嵌一个 `tomcat` 和 `jetty` 等服务器，
因此，你的 spring boot 应用程序不需要单独的外部服务器即可运行;
同时 spring-boot-starter-web 还是 springMVC 式的 spring boot 应用

2. 将应用程序打包成一个 jar，并使用 内嵌 HTTP 服务器的一个最大好处是，
你可以像其它方式那样运行你的应用程序。调试 spring boot 应用也很简单，你都
不需要任何特殊 IDE 插件或扩展。本章节只介绍基于 jar 的打包，如果你选择将
应用打包成 war 文件，你最好参考相关的文档。

3. 我们可以使用 mvn spring-boot:run (前提是 导入了
spring-boot-maven-plugin 依赖，因为 run 命令是这个插件的内容)来运行一个 spring boot 的 入口类

4. spring boot 应用程序可以打包成 一个 jar 文件，是因为我们引入了
spring-boot-maven-plugin 依赖，该插件其实并不依赖于外部的 maven 插件

5. 由于 spring boot 应用只是普通的 Java 应用，所以 jvm 热交换(hot-swapping) 也能开箱即用。
不过 jvm 热交换能力有限，想要彻底的解决方案，推荐使用 spring-boot-devtools 模块，该模块
也支持应用快速重启(restart)

6. spring boot 包含了一些额外的工具类，用于提升 spring boot 应用的开发体验。
spring-boot-devtools 模块可以 included 到任何模块中，以提供 development-time 特性，
只要 classpath 下的文件有变动，它就会自动重启
你只需要简单的将该模块的依赖添加到构建中即可。例如 maven 构建的项目中：
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```
设置 optional 项，是为了防止 devtools 传递到项目中的其它模块,从而设置了这个依赖级别,
如果想 devtools 绝对不包含在一个产品级构建中，你可以使用 excludeDevtools 构建属性
彻底移除该 jar

7. 禁用重启。
如果你需要彻底禁用重启支持，比如，不能跟某个特殊库一块工作，你需要在调用 SpringApplication.run(...) 之前
设置一个系统属性，如下：
public static void main(String[] args){
    System.setProperty("Spring.devtools.restart.enabled", "false");
    SpringApplication.run(MyApp.class, args);
}

8. 自定义 Banner
通过在 classpath 下添加一个 banner.txt 或设置 banner.location 来指定相应的
文件可以改变启动过程中打印的 banner。如果这个文件有特殊的编码，你可以使用
banner.encoding 来设置它(默认为 utf8)。除了文本文件外，你也可以添加一个
banner.gif， banner.jpg等
注意，这里的 classpath 指的是，你将 banner.txt 放在 resources 目录下，而不是 classpath 
的源码目录下【注意位置】, 但是我在试验 banner.jpg 时并没有成功
```java
public class ApplicationMain {

    public static void main(String[] args){
//        try {
//            SpringApplication.run(ApplicationMain.class, args);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }

        SpringApplication app = new SpringApplication(ApplicationMain.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }
}
```

9. 如果你需要在 SpringApplication 启动后执行一些特殊的代码，你可以实现 ApplicationRunner 或
CommandLineRunner 接口，这两个接口工作方式相同，都只提供单一的 run 方法，该方法仅在
SpringApplication.run() 完成之前调用.例如：
```java
@Component
public class MyBean implements CommandLineRunner {

    public void run(String... args){
        // Do something
        System.out.println("我在 SpringApplication.run() 之前执行...");
    }
}
```

10. 如果某些定义的 CommandLineRunner 或 ApplicationRunner beans 需要以特定的顺序调用，
你可以实现 org.springframework.core.Ordered 接口或使用 org.springframework.core.annotation.Ordered 注解

11. Application 退出
如果想在应用结束时返回特定的退出码(exit code)，这些 beans 可以实现 org.springframework.boot.ExitCodeGenerator 接口

12. 外部化配置
Spring Boot 允许你将配置外部化(externalize)，这样你就能够在不同的环境下使用相同的代码。
你可以使用 properties 文件，YAML 文件，环境变量和命令行参数来外部化配置。
使用 @Value 注解，可以直接将属性值注入到 beans 中，然后通过 Spring 的 Environment 抽象
或通过 @ConfigurationProperties 绑定到结构化对象来访问。例如：
```java
@org.springframework.stereotype.Component
public class MyBean{
    
    @Value("${name}")
    private String name;
}
```
你可以将一个 application.properties 或 application.yaml 文件放到应用的 classpath 下，
为 name 提供一个合适的默认属性值，当在新的环境中运行时，可以在 jar 包外提供一个 application.preperties/
application.yaml 覆盖 name 属性，对于一次性的测试，你可以使用特定的命令行开关启动应用，比如
`java -jar app.jar --name="Spring"`

13. 配置随机值
在注入随机值（比如：密钥或者测试用例）时 RandomValuePropertySource 很有用，
它能产生整数，longs 或 字符串,比如：
```properties
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.number.less.than.ten=${random.int(10)}
my.number.in.range=${random.int[1024, 65536]}
```

14. 访问命令行属性
默认情况下，SpringApplication 会将所有命令行配置参数(以 '-' 开头，比如
--server.port=9000) 转换成一个 property，并将其添加到 Spring Environment 中。
命令行属性总是优于其它属性源。如果不想将命令行属性添加到 Environment，你可以使用
SpringApplication.setAddCommandLineProperties(false) 来禁用它们

15. Application 属性文件
SpringApplication 将从以下位置加载 application.properties 文件，并把它们添加到
 Spring Environment 中：
 * 当前目录下的 /config 子目录
 * 当前目录
 * classpath 下的 /config 包
 * classpath 根路径
如果你不喜欢将 application.properties(application.yaml) 作为配置文件名，你可以通过指定
spring.config.name 环境属性来切换其它的名称，也可以使用 spring.config.location 环境
属性引用一个明确的路径(目录位置或文件路径列表以逗号分隔)

16. 文件输出
默认情况下，Spring Boot 只会将日志记录到控制台，而不写进日志文件，如果需要，
你可以设置 logging.file 或 logging.path 属性（例如 application.preperties）

17. 开发web应用
Spring Boot 非常适合开发web应用，你可以使用内嵌的Tomcat，Jetty或者
Undertow轻轻松松地创建一个HTTP服务器，大多数的web应用都可以使用
spring-boot-starter-web 模块进行快速搭建和运行

18. Spring Web MVC 框架
Spring Web MVC框架（通常简称为 SpringMVC）是一个富模型、视图、
控制器 的 web 框架，允许用户创建特定的 @Controller 或 @RestController beans 来
处理传入的 HTTP 请求，通过 @RequestMapping 注解可以将控制器中的方法映射到
相应的 HTTP 请求。例如：
```java
@RestController
@RequestMapping(value="/users")
public class MyRestController {
@RequestMapping(value="/{user}", method=RequestMethod.GET)
public User getUser(@PathVariable Long user) {
// ...
}
@RequestMapping(value="/{user}/customers", method=RequestMet
hod.GET)
List<Customer> getUserCustomers(@PathVariable Long user) {
// ...
}
@RequestMapping(value="/{user}", method=RequestMethod.DELETE
)
public User deleteUser(@PathVariable Long user) {
// ...
}
}
```
SpringMVC 是 Spring 框架的核心部分

19. HttpMessageConverters
SpringMVC 使用 HttpMessageConverters 接口转换 HTTP 请求和响应，合适的默认配置可以开箱即用，
例如对象自动转换为 JSON(使用 Jackson 库)或 XML（如果 Jackson XML 扩展可用，否则使用 JAXB）,
字符串默认使用 UTF-8 编码
可以使用 Spring Boot 的 HttpMessageConverters 类添加或自定义转换类：
```java
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.*;

@Configuration
public class MyConfiguration{
    
    @Bean
    public HttpMessageConverters customConverters(){
        HttpMessageConverters<?> additional = ...;
        HttpMessageConverters<?> another = ...;
        return new HttpMessageConverters(additional, another);
    }
}
```
上下文中出现的所有 HTTPMessageConverter bean 都将添加到 converters 列表，
你可以通过这种方式覆盖默认的转换器列表（converters）

20 CORS 支持
跨域资源共享（CORS），SpringMVC 对 cors 提供开箱即用的支持，不用添加任何特殊配置，
只需要在 Spring Boot 应用的 controller 方法上注解 @CrossOrigin ，并添加 CORS 配置

21. 内嵌 servlet 容器支持
Spring Boot 支持内嵌的 tomcat，jetty 和 undertow 服务器，多数开发者只需要使用合适的
'Starter' 来获取一个完全配置好的实例即可，内嵌服务器默认监听8080端口的HTTP请求

22. 安全
如果添加了 Spring Security 的依赖，那么 web 应用默认对所有的 HTTP 路径（也称为终点，端点，
表示 API 的具体网址）使用 'basic' 认证，为了给 web 应用添加方法级别（method-level）的保护，
你可以添加 @EnableGlobalMethodSecurity 并使用想要的设置
默认的 AuthenticationManager 只有一个用户（'user' 的用户名和随机密码会在应用启动时
以 INFO 日志级别打印出来）

23. Solr
Apache Solr 是一个搜索引擎， Spring Boot 为 Solr5 客户端 library 提供基本的自动配置，
连接 Solr
你可以注入一个自动配置的 SolrClient 实例，就像其它 Spring beans 那样，该实例
默认使用 localhost：8083/solr 连接 Solr 服务器：
```java
@Component
public class MyBean{
    private SolrClient solr;
    
    @Autowired
    public MyBean(SolrClient solr){
        this.solr = solr;
    }
}
```

24. Elasticsearch
ElasticSearch 是一个开源的，分布式的，实时搜索和分析引擎。Spring Boot 为 ElasticSearch 提供
基本的自动配置，Spring Data Elasticsearch 提供在它之上的抽象，还有用于收集依赖的
spring-boot-starter-data-elasticsearch 

25. 使用 Jest 连接 Elasticsearch
如果添加 Jest 依赖，你可以注入一个自动配置的 JestClient，默认目标是
http://localhost:9200/,你也可以进一步配置该客户端：
```properties
spring.elasticsearch.jest.uris = http://search.example.com:9200
spring.elasticsearch.jest.read-timeout = 10000
spring.elasticsearch.jest.username = user
spring.elasticsearch.jest.password = secret
```

25. 使用 Spring Data 连接 Elasticsearch
你可以注入一个自动配置的 Elasticsearch 或 ElasticsearchClient 实例，
就像其它 Spring Bean 那样那样，该实例默认内嵌一个本地，内存型服务器（
在 Elasticsearch 中被称为 Node），并使用当前工作目录作为服务器的 home 目录。
在这个步骤中，首先要做的是告诉 Elasticsearch 将文件存放到什么地方
spring.data.elasticsearch.properties.path.home=/foo/bar
另外，你可以通过设置 spring.data.elasticsearch.cluster-nodes(逗号
分割的 host:port 列表)来切换远程服务器
spring.data.elasticsearch.cluster-nodes=localhost:9300
```java
@Component
public class MyBean{
    private ElasticsearchTemplate template;
    
    @Autowired
    public MyBean(ElasticsearchTemplate template){
        this.template = template;
    }
}
```

26. 测试 Spring 应用
依赖注入主要优势之一就是它能够让你的代码更容易进行单元测试。你只需要简单地通过 
new 操作符实例化对象，甚至不需要涉及 Spring，也可以使用模拟对象替换真正的依赖。
你常常需要在进行单元测试后，开始集成测试（在这个过程中只需要涉及到 Spring 的 ApplicationContext）。
在执行集成测试时，不需要部署应用或连接到其它基础设施是非常有用的，Spring 框架为实现这样的
集成测试提供了一个专用的测试模块，通过声明 org.springframework:spring-test 的依赖，
或使用 Spring-boot-starter-test 'Starter' 就可以使用它了


27. 测试 Spring Boot 应用
SpringBoot 应用只是一个 Spring ApplicationContext，所以在测试时对它只需要像处理
普通 Spring context 那样即可。












































