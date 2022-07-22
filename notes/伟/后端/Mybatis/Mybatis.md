# Mybatis简介

## 什么是MyBatis

- MyBatis 是一款优秀的持久层框架 
- MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集的过程 
- MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 实体类 【Plain Old Java Objects,普通的 Java对象】映射成数据库中的记录。 
- MyBatis 本是apache的一个开源项目ibatis, 2010年这个项目由apache 迁移到了google code，并且改名为MyBatis 。 
- 2013年11月迁移到Github .
- Mybatis官方文档 : http://www.mybatis.org/mybatis-3/zh/index.html 
- GitHub : https://github.com/mybatis/mybatis-3

## 持久化

- 持久化是将程序数据在持久状态和瞬时状态间转换的机制。
  - 即把数据（如内存中的对象）保存到可永久保存的存储设备中（如磁盘）。持久化的主要应用 是将内存中的对象存储在数据库中，或者存储在磁盘文件中、XML数据文件中等等。
  - JDBC就是一种持久化机制。文件IO也是一种持久化机制。
- 为什么需要持久化服务呢？那是由于内存本身的缺陷引起的
  - 内存断电后数据会丢失，但有一些对象是无论如何都不能丢失的，比如银行账号等，遗憾的是，人们还无法保证内存永不掉电。
  - 内存过于昂贵，与硬盘、光盘等外存相比，内存的价格要高2~3个数量级，而且维持成本也 高，至少需要一直供电吧。所以即使对象不需要永久保存，也会因为内存的容量限制不能一直 呆在内存中，需要持久化来缓存到外存。

## 持久层

- 完成持久化工作的代码块 . ----> dao层 【DAO (Data Access Object) 数据访问对象】 
- 大多数情况下特别是企业级应用，数据持久化往往也就意味着将内存中的数据保存到磁盘上加以固化，而持久化的实现过程则大多通过各种**关系数据库**来完成。
- 不过这里有一个字需要特别强调，也就是所谓的“层”。对于应用系统而言，数据持久功能大多是必不可少的组成部分。也就是说，我们的系统中，已经天然的具备了“持久层”概念？也许是，但也许实际情况并非如此。之所以要独立出一个“持久层”的概念,而不是“持久模块”，“持久单元”，也就意味着，我们的系统架构中，应该有一个相对独立的逻辑层面，专著于数据持久化逻辑的实现. 
- 与系统其他部分相对而言，这个层面应该具有一个较为清晰和严格的逻辑边界。 【说白了就是用来操作数据库存在的！】

## 为什么需要Mybatis

- Mybatis就是帮助程序猿将数据存入数据库中 , 和从数据库中取数据 
- 传统的jdbc操作 , 有很多重复代码块 .比如 : 数据取出时的封装 , 数据库的建立连接等等... , 通过框架可以减少重复代码,提高开发效率
- MyBatis 是一个半自动化的**ORM框架** (Object Relationship Mapping) -->**对象关系映射**
- 所有的事情，不用Mybatis依旧可以做到，只是用了它，所有实现会更加简单！
- MyBatis的优点
  - 简单易学：本身就很小且简单。没有任何第三方依赖，最简单安装只要两个jar文件+配置几个 sql映射文件就可以了，易于学习，易于使用，通过文档和源代码，可以比较完全的掌握它的设计思路和实现。
  - 灵活：mybatis不会对应用程序或者数据库的现有设计强加任何影响。 sql写在xml里，便于统一管理和优化。通过sql语句可以满足操作数据库的所有需求。
  - 解除sql与程序代码的耦合：通过提供DAO层，将业务逻辑和数据访问逻辑分离，使系统的设计更清晰，更易维护，更易单元测试。sql和代码的分离，提高了可维护性。
  - 提供xml标签，支持编写动态sql。

# CRUD操作

## namespace

配置文件中namespace中的名称为对应Mapper接口或者Dao接口的完整包名,必须一致！

## select

- select标签是mybatis中最常用的标签之一

- select语句有很多属性可以详细配置每一条SQL语句

  - id

    - 命名空间中唯一的标识符
    - 接口中的方法名与映射文件中的SQL语句ID 一一对应

  - parameterType：传入SQL语句的参数类型 。【万能的Map，可以多尝试使用】

    ```java
    //通过密码和名字查询用户
    User selectUserByNP(@Param("username") String username,@Param("pwd") String pwd);
    
    //使用万能的Map
    User selectUserByNP2(Map<String,Object> map);
    ```

  - resultType：SQL语句返回值类型。【完整的类名或者别名】

## insert

我们一般使用insert标签进行插入操作，它的配置和select标签差不多！

```xml
<insert id="addUser" parameterType="com.wei.pojo.User">
	insert into user (id,name,pwd) values (#{id},#{name},#{pwd})
</insert>
```

## update

```xml
<update id="updateUser" parameterType="com.wei.pojo.User">
	update user set name=#{name},pwd=#{pwd} where id = #{id}
</update>
```

## delete

```xml
<delete id="deleteUser" parameterType="int">
	delete from user where id = #{id}
</delete>
```

## 小结

- 所有的增删改操作都需要提交事务！ 
- 接口所有的普通参数，尽量都写上@Param参数，尤其是多个参数时，必须写上！ 
- 有时候根据业务的需求，可以考虑使用map传递参数！ 
- 为了规范操作，在SQL的配置文件中，我们尽量将Parameter参数和resultType都写上！

# 配置解析

## 核心配置文件

- mybatis-config.xml 系统核心配置文件
- MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。
- 能配置的内容如下：

```xml
configuration（配置）
    properties（属性）
    settings（设置）
    typeAliases（类型别名）
    typeHandlers（类型处理器）
    objectFactory（对象工厂）
    plugins（插件）
    environments（环境配置）
        environment（环境变量）
            transactionManager（事务管理器）
            dataSource（数据源）
	databaseIdProvider（数据库厂商标识）
	mappers（映射器）
<!-- 注意元素节点的顺序！顺序不对会报错 -->
```

## environments元素

```xml
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC">
        	<property name="..." value="..."/>
        </transactionManager>
        <dataSource type="POOLED">
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
        </dataSource>
    </environment>
</environments>
```

- 配置MyBatis的多套运行环境，将SQL映射到多个不同的数据库上，必须指定其中一个为默认运行环境（通过default指定）

- 子元素节点：environment

  - 具体的一套环境，通过设置id进行区别，id保证唯一！

  - 子元素节点：transactionManager - [ 事务管理器 ]

    ```xml
    <!-- 语法 -->
    <transactionManager type="[ JDBC | MANAGED ]"/>
    ```

  - 子元素节点：数据源（dataSource）

    - dataSource 元素使用标准的 JDBC 数据源接口来配置 JDBC 连接对象的资源。

    - 数据源是必须配置的。

    - 有三种内建的数据源类型

      ```xml
      type="[UNPOOLED|POOLED|JNDI]"）
      ```

      - **unpooled**： 这个数据源的实现只是每次被请求时打开和关闭连接。
      - **pooled**： 这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来 , 这是一种使得并发 Web 应用快速响应请求的流行处理方式。
      - **jndi**：这个数据源的实现是为了能在如 Spring 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的引用。
      - 数据源也有很多第三方的实现，比如dbcp，c3p0，druid等等....

## mappers元素

### mappers

- 映射器 : 定义映射SQL语句文件
- 既然 MyBatis 的行为其他元素已经配置完了，我们现在就要定义 SQL 映射语句了。但是首先我们 需要告诉 MyBatis 到哪里去找到这些语句。 Java 在自动查找这方面没有提供一个很好的方法，所 以最佳的方式是告诉 MyBatis 到哪里去找映射文件。你可以使用相对于类路径的资源引用， 或完 全限定资源定位符（包括 file:/// 的 URL），或类名和包名等。映射器是MyBatis中最核心 的组件之一，在MyBatis 3之前，只支持xml映射器，即：所有的SQL语句都必须在xml文件中配 置。而从MyBatis 3开始，还支持接口映射器，这种映射器方式允许以Java代码的方式注解定义SQL 语句，非常简洁。

### 引入资源方式

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
	<mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>

<!-- 使用完全限定资源定位符（URL） -->
<mappers>
	<mapper url="file:///var/mappers/AuthorMapper.xml"/>
</mappers>

<!--
使用映射器接口实现类的完全限定类名
需要配置文件名称和接口名称一致，并且位于同一目录下
-->
<mappers>
	<mapper class="org.mybatis.builder.AuthorMapper"/>
</mappers>

<!--
将包内的映射器接口实现全部注册为映射器
但是需要配置文件名称和接口名称一致，并且位于同一目录下
-->
<mappers>
	<package name="org.mybatis.builder"/>
</mappers>
```

### Mapper文件

````xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.kuang.mapper.UserMapper">

</mapper>
````

- namespace中文意思：命名空间，作用如下：
- namespace和子元素的id联合保证唯一 , 区别不同的mapper
- 绑定DAO接口
  - namespace的命名必须跟某个接口同名
  - 接口中的方法与映射文件中sql语句id应该一一对应
- namespace命名规则 : 包名+类名

MyBatis 的真正强大在于它的映射语句，这是它的魔力所在。由于它的异常强大，映射器的 XML 文件就 显得相对简单。如果拿它跟具有相同功能的 JDBC 代码进行对比，你会立即发现省掉了将近 95% 的代 码。MyBatis 为聚焦于 SQL 而构建，以尽可能地为你减少麻烦。

## Properties优化

数据库这些属性都是可外部配置且可动态替换的，既可以在典型的 Java 属性文件中配置，亦可通过 properties 元素的子元素来传递。

1. 在资源目录下新建一个db.properties

   ````properties
   driver=com.mysql.jdbc.Driver
   url=jdbc:mysql://localhost:3306/mybatis?
   useSSL=true&useUnicode=true&characterEncoding=utf8
   username=root
   password=123456
   ````

2. 将文件导入properties 配置文件

   ````xml
   <configuration>
       <!--导入properties文件-->
       <properties resource="db.properties"/>
       
       <environments default="development">
           <environment id="development">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="${driver}"/>
                   <property name="url" value="${url}"/>
                   <property name="username" value="${username}"/>
                   <property name="password" value="${password}"/>
               </dataSource>
           </environment>
       </environments>
       <mappers>
       	<mapper resource="mapper/UserMapper.xml"/>
       </mappers>
   </configuration>
   
   ````

## typeAliases优化

类型别名是为 Java 类型设置一个短的名字。它只和 XML 配置有关，存在的意义仅在于用来减少类完全限定名的冗余。

````xml
<!--配置别名,注意顺序-->
<typeAliases>
	<typeAlias type="com.kuang.pojo.User" alias="User"/>
</typeAliases>
````

当这样配置时， **User** 可以用在任何使用 **com.kuang.pojo.User** 的地方。 也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean，比如:

````xml
<typeAliases>
	<package name="com.kuang.pojo"/>
</typeAliases>
````

每一个在包 **com.kuang.pojo** 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的 非限定类名来作为它的别名。 

若有注解，则别名为其注解值。见下面的例子：

````xml
@Alias("user")
public class User {
	...
}
````

## 其他配置浏览

### 设置

- 懒加载 

- 日志实现 

- 缓存开启关闭

- 一个配置完整的 settings 元素的示例如下：

  ````xml
  <settings>
      <setting name="cacheEnabled" value="true"/>
      <setting name="lazyLoadingEnabled" value="true"/>
      <setting name="multipleResultSetsEnabled" value="true"/>
      <setting name="useColumnLabel" value="true"/>
      <setting name="useGeneratedKeys" value="false"/>
      <setting name="autoMappingBehavior" value="PARTIAL"/>
      <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
      <setting name="defaultExecutorType" value="SIMPLE"/>
      <setting name="defaultStatementTimeout" value="25"/>
      <setting name="defaultFetchSize" value="100"/>
      <setting name="safeRowBoundsEnabled" value="false"/>
      <setting name="mapUnderscoreToCamelCase" value="false"/>
      <setting name="localCacheScope" value="SESSION"/>
      <setting name="jdbcTypeForNull" value="OTHER"/>
      <setting name="lazyLoadTriggerMethods"
      value="equals,clone,hashCode,toString"/>
  </settings>
  ````

### 类型处理器

- 无论是 MyBatis 在预处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出 一个值时， 都会用类型处理器将获取的值以合适的方式转换成 Java 类型。 
- 你可以重写类型处理器或创建你自己的类型处理器来处理不支持的或非标准的类型。【了解即可】

### 对象工厂

- MyBatis 每次创建结果对象的新实例时，它都会使用一个对象工厂（ObjectFactory）实例来完成。 
- 默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过有参构造方法来实例化。 
- 如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现。【了解即可】

## 生命周期和作用域

### 作用域（Scope）和生命周期

理解我们目前已经讨论过的不同作用域和生命周期类是至关重要的，因为错误的使用会导致非常严重的并发问题。

我们可以先画一个流程图，分析一下Mybatis的执行过程！

![image-20220722160042318](G:\Agogogo2022\project\studyNotes\notes\伟\后端\Mybatis\pictures\image-20220722160042318.png)

### 作用域理解

- SqlSessionFactoryBuilder 的作用在于创建 SqlSessionFactory，创建成功后， SqlSessionFactoryBuilder 就失去了作用，所以它只能存在于创建 SqlSessionFactory 的方法中， 而不要让其长期存在。因此 **SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域**（也就是局部方法变量）。
- SqlSessionFactory 可以被认为是一个数据库连接池，它的作用是创建 SqlSession 接口对象。因为 MyBatis 的本质就是 Java 对数据库的操作，所以 SqlSessionFactory 的生命周期存在于整个 MyBatis 的应用之中，所以一旦创建了 SqlSessionFactory，就要长期保存它，直至不再使用 MyBatis 应用，所以可以认为 SqlSessionFactory 的生命周期就等同于 MyBatis 的应用周期。
- 由于 SqlSessionFactory 是一个对数据库的连接池，所以它占据着数据库的连接资源。如果创建多 个 SqlSessionFactory，那么就存在多个数据库连接池，这样不利于对数据库资源的控制，也会导 致数据库连接资源被消耗光，出现系统宕机等情况，所以尽量避免发生这样的情况。
- 因此在一般的应用中我们往往希望 SqlSessionFactory 作为一个单例，让它在应用中被共享。**所以说 SqlSessionFactory 的最佳作用域是应用作用域。**
- 如果说 SqlSessionFactory 相当于数据库连接池，那么 SqlSession 就相当于一个数据库连接 （Connection 对象），你可以在一个事务里面执行多条 SQL，然后通过它的 commit、rollback 等方法，提交或者回滚事务。所以它应该存活在一个业务请求中，处理完整个请求后，应该关闭这 条连接，让它归还给 SqlSessionFactory，否则数据库资源就很快被耗费精光，系统就会瘫痪，所 以用 try...catch...finally... 语句来保证其正确关闭。
- **所以 SqlSession 的最佳的作用域是请求或方法作用域。**

![image-20220722160416608](G:\Agogogo2022\project\studyNotes\notes\伟\后端\Mybatis\pictures\image-20220722160416608.png)

# ResultMap

要解决的问题：属性名和字段名不一致

**解决方案**

- 为列名指定别名 , 别名和java实体类的属性名一致

  ````xml
  <select id="selectUserById" resultType="User">
  	select id , name , pwd as password from user where id = #{id}
  </select>
  ````

- 使用结果集映射->ResultMap 【推荐】

  ````xml
  <resultMap id="UserMap" type="User">
      <!-- id为主键 -->
      <id column="id" property="id"/>
      <!-- column是数据库表的列名 , property是对应实体类的属性名 -->
      <result column="name" property="name"/>
      <result column="pwd" property="password"/>
  </resultMap>
  <select id="selectUserById" resultMap="UserMap">
  	select id , name , pwd from user where id = #{id}
  </select>
  ````

## 自动映射

- resultMap 元素是 MyBatis 中最重要最强大的元素。它可以让你从 90% 的 JDBC ResultSets 数据提取代码中解放出来。
- 实际上，在为一些比如连接的复杂语句编写映射代码的时候，一份 resultMap 能够代替实现同 等功能的长达数千行的代码。
- ResultMap 的设计思想是，对于简单的语句根本不需要配置显式的结果映射，而对于复杂一点的语 句只需要描述它们的关系就行了。

你已经见过简单映射语句的示例了，但并没有显式指定 resultMap 。比如：

````xml
<select id="selectUserById" resultType="map">
    select id , name , pwd
    from user
    where id = #{id}
</select>
````

上述语句只是简单地将所有的列映射到 HashMap 的键上，这由 resultType 属性指定。虽然在 大部分情况下都够用，但是 HashMap 不是一个很好的模型。你的程序更可能会使用 JavaBean 或 POJO（Plain Old Java Objects，普通老式 Java 对象）作为模型。

## 手动映射

````xml
<resultMap id="UserMap" type="User">
    <!-- id为主键 -->
    <id column="id" property="id"/>
    <!-- column是数据库表的列名 , property是对应实体类的属性名 -->
    <result column="name" property="name"/>
    <result column="pwd" property="password"/>
</resultMap>
<select id="selectUserById" resultMap="UserMap">
	select id , name , pwd from user where id = #{id}
</select>
````

# 分页的实现

## 日志工厂

对于以往的开发过程，我们会经常使用到debug模式来调节，跟踪我们的代码执行过程。但是现在使用 Mybatis是基于接口，配置文件的源代码执行过程。因此，我们必须选择日志工具来作为我们开发，调节程序的工具。

Mybatis内置的日志工厂提供日志功能，具体的日志实现有以下几种工具：

- SLF4J 
- Apache 
- Commons 
- Logging 
- Log4j 2 
- Log4j 
- JDK 
- logging

具体选择哪个日志实现工具由MyBatis的内置日志工厂确定。它会使用最先找到的（按上文列举的顺序查找）。 如果一个都未找到，日志功能就会被禁用。

**标准日志实现**

指定 MyBatis 应该使用哪个日志记录实现。如果此设置不存在，则会自动发现日志记录实现。

````xml
<settings>
	<setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
````

## Log4j

- Log4j是Apache的一个开源项目 
- 通过使用Log4j，我们可以控制日志信息输送的目的地：控制台，文本，GUI组件.... 
- 我们也可以控制每一条日志的输出格式；
- 通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程。最令人感兴趣的就 是，这些可以通过一个配置文件来灵活地进行配置，而不需要修改应用的代码。

**使用步骤：**

1. 导入log4j的包

   ````xml
   <dependency>
       <groupId>log4j</groupId>
       <artifactId>log4j</artifactId>
       <version>1.2.17</version>
   </dependency>
   ````

2. 配置文件编写

   ````properties
   #将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
   log4j.rootLogger=DEBUG,console,file
   #控制台输出的相关设置
   log4j.appender.console = org.apache.log4j.ConsoleAppender
   log4j.appender.console.Target = System.out
   log4j.appender.console.Threshold=DEBUG
   log4j.appender.console.layout = org.apache.log4j.PatternLayout
   log4j.appender.console.layout.ConversionPattern=[%c]-%m%n
   #文件输出的相关设置
   log4j.appender.file = org.apache.log4j.RollingFileAppender
   log4j.appender.file.File=./log/kuang.log
   log4j.appender.file.MaxFileSize=10mb
   log4j.appender.file.Threshold=DEBUG
   log4j.appender.file.layout=org.apache.log4j.PatternLayout
   log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n
   #日志输出级别
   log4j.logger.org.mybatis=DEBUG
   log4j.logger.java.sql=DEBUG
   log4j.logger.java.sql.Statement=DEBUG
   log4j.logger.java.sql.ResultSet=DEBUG
   log4j.logger.java.sql.PreparedStatement=DEBUG
   ````

3. setting设置日志实现

   ````xml
   <settings>
   	<setting name="logImpl" value="LOG4J"/>
   </settings>
   ````

## limit实现分页

````java
//分页查询 , 两个参数startIndex , pageSize
@Test
public void testSelectUser() {
    SqlSession session = MybatisUtils.getSession();
    UserMapper mapper = session.getMapper(UserMapper.class);
    int currentPage = 1; //第几页
    int pageSize = 2; //每页显示几个
    Map<String,Integer> map = new HashMap<String,Integer>();
    map.put("startIndex",(currentPage-1)*pageSize);
    map.put("pageSize",pageSize);
    List<User> users = mapper.selectUser(map);
    for (User user: users){
    	System.out.println(user);
    }
    session.close();
}
````

## PageHelper

了解即可，可以自己尝试使用 官方文档：https://pagehelper.github.io/

# 使用注解开发

## 面向接口编程

- 大家之前都学过面向对象编程，也学习过接口，但在真正的开发中，很多时候我们会选择面向接口编程 
- **根本原因 : 解耦 , 可拓展 , 提高复用 , 分层开发中 , 上层不用管具体的实现 , 大家都遵守共同的标准 , 使得开发变得容易 , 规范性更好** 
- 在一个面向对象的系统中，系统的各种功能是由许许多多的不同对象协作完成的。在这种情况下， 各个对象内部是如何实现自己的,对系统设计人员来讲就不那么重要了； 
- 而各个对象之间的协作关系则成为系统设计的关键。小到不同类之间的通信，大到各模块之间的交 互，在系统设计之初都是要着重考虑的，这也是系统设计的主要工作内容。面向接口编程就是指按 照这种思想来编程。

**关于接口的理解**

- 接口从更深层次的理解，应是定义（规范，约束）与实现（名实分离的原则）的分离。 
- 接口的本身反映了系统设计人员对系统的抽象理解。 
- 接口应有两类：
  - 第一类是对一个个体的抽象，它可对应为一个抽象体(abstract class)
  - 第二类是对一个个体某一方面的抽象，即形成一个抽象面（interface）
- 一个体有可能有多个抽象面。抽象体与抽象面是有区别的。

**三个面向区别**

- 面向对象是指，我们考虑问题时，以对象为单位，考虑它的属性及方法 
- 面向过程是指，我们考虑问题时，以一个具体的流程（事务过程）为单位，考虑它的实现
- 接口设计与非接口设计是针对复用技术而言的，与面向对象（过程）不是一个问题.更多的体现就是对系统整体的架构

## 利用注解开发

- mybatis最初配置信息是基于 XML ,映射语句(SQL)也是定义在 XML 中的。而到MyBatis 3提供了 新的基于注解的配置。不幸的是，Java 注解的的表达力和灵活性十分有限。最强大的 MyBatis 映 射并不能用注解来构建
- sql 类型主要分成 :
  - @select ()
  - @update ()
  - @Insert ()
  - @delete ()

【注意】利用注解开发就不需要mapper.xml映射文件了 

````java
//查询全部用户
@Select("select id,name,pwd password from user")
public List<User> getAllUser();

//添加一个用户
@Insert("insert into user (id,name,pwd) values (#{id},#{name},#{pwd})")
int addUser(User user);

//修改一个用户
@Update("update user set name=#{name},pwd=#{pwd} where id = #{id}")
int updateUser(User user);

//根据id删除用
@Delete("delete from user where id = #{id}")
int deleteUser(@Param("id")int id);
````

本质上利用了jvm的动态代理机制

![image-20220722165833756](G:\Agogogo2022\project\studyNotes\notes\伟\后端\Mybatis\pictures\image-20220722165833756.png)

 **Mybatis详细的执行流程**

![image-20220722165925509](G:\Agogogo2022\project\studyNotes\notes\伟\后端\Mybatis\pictures\image-20220722165925509.png)

## 关于@Param

- 在方法只接受一个参数的情况下，可以不使用@Param。 

- 在方法接受多个参数的情况下，建议一定要使用@Param注解给参数命名。 

- 如果参数是 JavaBean ， 则不能使用@Param。

- 不使用@Param注解时，参数只能有一个，并且是Javabean。

## \#与$的区别

- \#{} 的作用主要是替换预编译语句(PrepareStatement)中的占位符? ，自动拼接''【推荐使用】

  ````sql
  INSERT INTO user (name) VALUES (#{name});
  INSERT INTO user (name) VALUES (?);
  ````

- ${} 的作用是直接进行字符串替换

  ```sql
  INSERT INTO user (name) VALUES ('${name}');
  INSERT INTO user (name) VALUES ('wei');
  ```

# 多对一的处理

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.kuang.mapper.StudentMapper">
    <!--
    需求：获取所有学生及对应老师的信息
    思路：
    1. 获取所有学生的信息
    2. 根据获取的学生信息的老师ID->获取该老师的信息
    3. 思考问题，这样学生的结果集中应该包含老师，该如何处理呢，数据库中我们一般
    使用关联查询？
    1. 做一个结果集映射：StudentTeacher
    2. StudentTeacher结果集的类型为 Student
    3. 学生中老师的属性为teacher，对应数据库中为tid。
       多个 [1,...）学生关联一个老师=> 一对一，一对多
    4. 查看官网找到：association – 一个复杂类型的关联；使用它来处理关联查询
    -->
    <select id="getStudents" resultMap="StudentTeacher">
    	select * from student
    </select>
    <resultMap id="StudentTeacher" type="Student">
    <!--association关联属性 property属性名 javaType属性类型 column在多
    的一方的表中的列名-->
        <association property="teacher" column="tid" javaType="Teacher"
        select="getTeacher"/>
    </resultMap>
    <!--
    这里传递过来的id，只有一个属性的时候，下面可以写任何值
    association中column多参数配置：

    column="{key=value,key=value}"
	column="{id=tid,name=tid}"

    其实就是键值对的形式，key是传给下个sql的取值名称，value是片段一中sql查询的
    字段名。
    -->
    <select id="getTeacher" resultType="teacher">
    	select * from teacher where id = #{id}
    </select>
</mapper>
```

````xml
<!--
按查询结果嵌套处理
思路：
1. 直接查询出结果，进行结果集的映射
-->
<select id="getStudents2" resultMap="StudentTeacher2" >
	select s.id sid, s.name sname , t.name tname
	from student s,teacher t
	where s.tid = t.id
</select>
<resultMap id="StudentTeacher2" type="Student">
    <id property="id" column="sid"/>
    <result property="name" column="sname"/>
        <!--关联对象property 关联对象在Student实体类中的属性-->
        <association property="teacher" javaType="Teacher">
        	<result property="name" column="tname"/>
        </association>
</resultMap>
````

# 一对多的处理

````xml

<mapper namespace="com.kuang.mapper.TeacherMapper">
<!--
思路:
    1. 从学生表和老师表中查出学生id，学生姓名，老师姓名
    2. 对查询出来的操作做结果集映射
    	1. 集合的话，使用collection！
            JavaType和ofType都是用来指定对象类型的
            JavaType是用来指定pojo中属性的类型
            ofType指定的是映射到list集合属性中pojo的类型。
-->
<select id="getTeacher" resultMap="TeacherStudent">
    select s.id sid, s.name sname , t.name tname, t.id tid
    from student s,teacher t
    where s.tid = t.id and t.id=#{id}
</select>
<resultMap id="TeacherStudent" type="Teacher">
    <result property="name" column="tname"/>
    <collection property="students" ofType="Student">
        <result property="id" column="sid" />
        <result property="name" column="sname" />
        <result property="tid" column="tid" />
    </collection>
</resultMap>
</mapper>
````

````xml
<select id="getTeacher2" resultMap="TeacherStudent2">
	select * from teacher where id = #{id}
</select>
<resultMap id="TeacherStudent2" type="Teacher">
    <!--column是一对多的外键 , 写的是一的主键的列名-->
    <collection property="students" javaType="ArrayList"
    	ofType="Student" column="id" select="getStudentByTeacherId"/>
</resultMap>
<select id="getStudentByTeacherId" resultType="Student">
	select * from student where tid = #{id}
</select>
````

**小结**

1. 关联-association 
2. 集合-collection 
3. 所以association是用于一对一和多对一，而collection是用于一对多的关系 
4. JavaType和ofType都是用来指定对象类型的 
   - JavaType是用来指定pojo中属性的类型 
   - ofType指定的是映射到list集合属性中pojo的类型

# 动态SQL

## if 

````xml
<select id="queryBlogIf" parameterType="map" resultType="blog">
    select * from blog where
    <if test="title != null">
    	title = #{title}
    </if>
    <if test="author != null">
    	and author = #{author}
    </if>
</select>
````

## Where

```xml
<select id="queryBlogIf" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <if test="title != null">
        	title = #{title}
        </if>
        <if test="author != null">
        	and author = #{author}
        </if>
    </where>
</select>
```

这个“where”标签会知道如果它包含的标签中有返回值的话，它就插入一个‘where’。此外，如果标签返 回的内容是以AND 或OR 开头的，则它会剔除掉。

## Set

````xml
<!--注意set是用的逗号隔开-->
<update id="updateBlog" parameterType="map">
    update blog
    <set>
        <if test="title != null">
        	title = #{title},
        </if>
        <if test="author != null">
        	author = #{author}
        </if>
    </set>
    where id = #{id};
</update>
````

## choose

有时候，我们不想用到所有的查询条件，只想选择其中的一个，查询条件有一个满足即可，使用 choose 标签可以解决此类问题，类似于 Java 的 switch 语句

```xml
<select id="queryBlogChoose" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <choose>
            <when test="title != null">
            	title = #{title}
            </when>
            <when test="author != null">
            	and author = #{author}
            </when>
            <otherwise>
            	and views = #{views}
            </otherwise>
        </choose>
    </where>
</select>
```

## SQL片段

有时候可能某个 sql 语句我们用的特别多，为了增加代码的重用性，简化代码，我们需要将这些代码抽取出来，然后使用时直接调用。

```xml
<sql id="if-title-author">
    <if test="title != null">
    	title = #{title}
    </if>
    <if test="author != null">
    	and author = #{author}
    </if>
</sql>

<select id="queryBlogIf" parameterType="map" resultType="blog">
	select * from blog
    <where>
    <!-- 引用 sql 片段，如果refid 指定的不在本文件中，那么需要在前面加上namespace-->
    	<include refid="if-title-author"></include>
    	<!-- 在这里还可以引用其他的 sql 片段 -->
    </where>
</select>
```

## Foreach

```xml
<select id="queryBlogForeach" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <!--
        collection:指定输入对象中的集合属性
        item:每次遍历生成的对象
        open:开始遍历时的拼接字符串
        close:结束时拼接的字符串
        separator:遍历对象之间需要拼接的字符串
        select * from blog where 1=1 and (id=1 or id=2 or id=3)
        -->
        <foreach collection="ids" item="id" open="and (" close=")"
        separator="or">
        	id=#{id}
        </foreach>
    </where>
</select>
```

# 缓存

## 简介

1. 什么是缓存 [ Cache ]？
   - 存在内存中的临时数据。
   - 将用户经常查询的数据放在缓存（内存）中，用户去查询数据就不用从磁盘上(关系型数据库 数据文件)查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。
2. 为什么使用缓存？
   - 减少和数据库的交互次数，减少系统开销，提高系统效率。
3. 什么样的数据能使用缓存？
   - 经常查询并且不经常改变的数据。

## Mybatis缓存

- MyBatis包含一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存。缓存可以极大的提升查询效率。
- MyBatis系统中默认定义了两级缓存：**一级缓存**和**二级缓存**
  - 默认情况下，只有一级缓存开启。（SqlSession级别的缓存，也称为本地缓存）
  - 二级缓存需要手动开启和配置，他是基于namespace级别的缓存。 
  - 为了提高扩展性，MyBatis定义了缓存接口Cache。我们可以通过实现Cache接口来自定义二级缓存

## 一级缓存

### 失效的四种情况

- 一级缓存是SqlSession级别的缓存，是一直开启的，我们关闭不了它；
- 一级缓存失效情况：没有使用到当前的一级缓存，效果就是，还需要再向数据库中发起一次查询请求！
  1. sqlSession不同：**每个sqlSession中的缓存相互独立**
  2. sqlSession相同，查询条件不同
  3. sqlSession相同，两次查询之间执行了增删改操作！
  4. sqlSession相同，手动清除一级缓存（session.clearCache();//手动清除缓存）

## 二级缓存

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存；
- 工作机制
  - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了；但是我们想要的是，会话关闭了，一 级缓存中的数据被保存到二级缓存中；
  - 新的会话查询信息，就可以从二级缓存中获取内容；
  - 不同的mapper查出的数据会放在自己对应的缓存（map）中；

### 使用步骤

1. 开启全局缓存 【mybatis-config.xml】

   ````xml
   <setting name="cacheEnabled" value="true"/>
   ````

2. 去每个mapper.xml中配置使用二级缓存，这个配置非常简单；【xxxMapper.xml】

   ````xml
   官方示例=====>查看官方文档
   <cache
       eviction="FIFO"
       flushInterval="60000"
       size="512"
       readOnly="true"/>
   这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。
   ````

## 缓存原理

![image-20220722175359834](G:\Agogogo2022\project\studyNotes\notes\伟\后端\Mybatis\pictures\image-20220722175359834.png)

## EhCache

Ehcache是一种广泛使用的java分布式缓存，用于通用缓存.

**步骤**

1. 要在应用程序中使用Ehcache，需要引入依赖的jar包

   ````xml
   <!-- https://mvnrepository.com/artifact/org.mybatis.caches/mybatisehcache -->
   <dependency>
       <groupId>org.mybatis.caches</groupId>
       <artifactId>mybatis-ehcache</artifactId>
       <version>1.1.0</version>
   </dependency>
   ````

2. 在mapper.xml中使用对应的缓存即可

   ````xml
   <mapper namespace = “org.acme.FooMapper” >
   	<cache type = “org.mybatis.caches.ehcache.EhcacheCache” />
   </mapper>
   ````

3. 编写ehcache.xml文件，如果在 `加载时` 未找到 `/ehcache.xml` 资源或出现问题，则将使用默认配置。

   ````xml
   <?xml version="1.0" encoding="UTF-8"?>
   <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
   updateCheck="false">
       <!--
       diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位
       置。参数解释如下：
       user.home – 用户主目录
       user.dir – 用户当前工作目录
       java.io.tmpdir – 默认临时文件路径
       -->
       <diskStore path="./tmpdir/Tmp_EhCache"/>
       <defaultCache
           eternal="false"
           maxElementsInMemory="10000"
           overflowToDisk="false"
           diskPersistent="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="259200"
           memoryStoreEvictionPolicy="LRU"/>
       <cache
           name="cloud_user"
           eternal="false"
           maxElementsInMemory="5000"
           overflowToDisk="false"
           diskPersistent="false"
           timeToIdleSeconds="1800"
           timeToLiveSeconds="1800"
           memoryStoreEvictionPolicy="LRU"/>
   <!--
   defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策
   略。只能定义一个。
   -->
   <!--
   name:缓存名称。
   maxElementsInMemory:缓存最大数目
   maxElementsOnDisk：硬盘最大缓存个数。
   eternal:对象是否永久有效，一但设置了，timeout将不起作用。
   overflowToDisk:是否保存到磁盘，当系统当机时
   timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当
   eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
   timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建
   时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
   diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store
   persists between restarts of the Virtual Machine. The default value is false.
   diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
   diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
   memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
   clearOnFlush：内存数量最大时是否清除。
   memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、
   FIFO（先进先出）、LFU（最少访问次数）。
   FIFO，first in first out，这个是大家最熟的，先进先出。
   LFU， Less Frequently Used，就是上面例子中使用的策略，直白一点就是讲一直以
   来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
   LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容
   量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。
   -->
   </ehcache>
   ````

   





