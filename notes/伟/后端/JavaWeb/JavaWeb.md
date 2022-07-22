# 基本概念

## 静态web

- *.htm, *.html,这些都是网页的后缀，如果服务器上一直存在这些东西，我们就可以直接进行读取。通络；

  ![image-20220721141225929](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721141225929.png)

- 静态web存在的缺点

  - Web页面无法动态更新，所有用户看到都是同一个页面
    - 轮播图，点击特效：伪动态 
    - JavaScript [实际开发中，它用的最多] 
    - VBScript
  - 它无法和数据库交互（数据无法持久化，用户无法交互）

## 动态web

页面会动态展示： “Web的页面展示的效果因人而异”；

![image-20220721141347365](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721141347365.png)

缺点：

停机维护，加入服务器的动态web资源出现了错误，我们需要重新编写我们的后台程序,重新发布；

优点：

- Web页面可以动态更新，所有用户看到都不是同一个页面

- 它可以与数据库交互 （数据持久化：注册，商品信息，用户信息........）

  ![image-20220721141502440](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721141502440.png)

# web服务器

服务器是一种被动的操作，用来处理用户的一些请求和给用户一些响应信息；

## Tomcat

![image-20220721141901038](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721141901038.png)

### 配置

![image-20220721141939740](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721141939740.png)

可以配置启动的端口号

- tomcat的默认端口号为：8080 
- mysql：3306 
- http：80 
- https：443

```xml
<Connector port="8081" protocol="HTTP/1.1"
connectionTimeout="20000"
redirectPort="8443" />
```

可以配置主机的名称

- 默认的主机名为：localhost->127.0.0.1
- 默认网站应用存放的位置为：webapps

```xml
<Host name="www.qinjiang.com" appBase="webapps"
unpackWARs="true" autoDeploy="true">
```

面试题：

请你谈谈网站是如何进行访问的！

1. 输入一个域名；回车

2.  检查本机的 C:\Windows\System32\drivers\etc\hosts配置文件下有没有这个域名映射

   1.  有：直接返回对应的ip地址，这个地址中，有我们需要访问的web程序，可以直接访问

   2. . 没有：去DNS服务器找，找到的话就返回，找不到就返回找不到；

      ![image-20220721142258858](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721142258858-16583845800221.png)

# Http

HTTP（超文本传输协议）是一个简单的请求-响应协议，它通常运行在TCP之上。 

- 文本：html，字符串，~ …. 
- 超文本：图片，音乐，视频，定位，地图……. 
- 80 

Https：安全的 

- 443

## 两个时代

- http1.0 

  HTTP/1.0：客户端可以与web服务器连接后，只能获得一个web资源，断开连接 

- http2.0 

  HTTP/1.1：客户端可以与web服务器连接后，可以获得多个web资源。

## Http请求

客户端---发请求（Request）---服务器

百度：

```xml
Request URL:https://www.baidu.com/ 请求地址
Request Method:GET get方法/post方法
Status Code:200 OK 状态码：200
Remote（远程） Address:14.215.177.39:443
```

- 请求行
  - 请求行中的请求方式：GET
  - 请求方式：Get，Post，HEAD,DELETE,PUT,TRACT…
    - get：请求能够携带的参数比较少，大小有限制，会在浏览器的URL地址栏显示数据内容，不 安全，但高效
    - post：请求能够携带的参数没有限制，大小没有限制，不会在浏览器的URL地址栏显示数据内容，安全，但不高效。
- 消息头

```xml
Accept：告诉浏览器，它所支持的数据类型
Accept-Encoding：支持哪种编码格式 GBK UTF-8 GB2312 ISO8859-1
Accept-Language：告诉浏览器，它的语言环境
Cache-Control：缓存控制
Connection：告诉浏览器，请求完成是断开还是保持连接
HOST：主机..../.
```

## Http响应

服务器---响应-----客户端

百度：

```xml
Cache-Control:private 缓存控制
Connection:Keep-Alive 连接
Content-Encoding:gzip 编码
Content-Type:text/html 类型
```

- 响应体

  ```xml
  Accept：告诉浏览器，它所支持的数据类型
  Accept-Encoding：支持哪种编码格式 GBK UTF-8 GB2312 ISO8859-1
  Accept-Language：告诉浏览器，它的语言环境
  Cache-Control：缓存控制
  Connection：告诉浏览器，请求完成是断开还是保持连接
  HOST：主机..../.
  Refresh：告诉客户端，多久刷新一次；
  Location：让网页重新定位；
  ```

- 响应状态码

  - 200：请求响应成功 200 
  - 3xx：请求重定向 
  - 4xx：找不到资源 
  - 5xx：服务器代码错误

# Maven

Maven的核心思想：**约定大于配置**

## 阿里云镜像

```xml
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>*,!jeecg,!jeecg-snapshots</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
</mirror>
```

## 本地仓库

```xml
<localRepository>D:\apache-maven-3.6.2\repository</localRepository>
```

## pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--Maven版本和头文件-->
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!--这里就是我们刚才配置的GAV-->
    <groupId>hongwei</groupId>
    <artifactId>javaweb-maven</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!--Package：项目的打包方式
    jar：java应用
    war：JavaWeb应用
    -->
    <packaging>war</packaging>
    <!--配置-->
    <properties>
        <!--项目的默认构建编码-->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!--编码版本-->
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    <!--项目依赖-->
    <dependencies>
        <!--具体依赖的jar包配置文件-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
        </dependency>
    </dependencies>
    <!--项目构建用的东西-->
    <build>
        <finalName>javaweb-01-maven</finalName>
        <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
            <plugins>
                <plugin>
                    <artifactId>maven-clean-plugin</artifactId>
                    <version>3.1.0</version>
                </plugin>
                <!-- see http://maven.apache.org/ref/current/maven-core/defaultbindings.html#Plugin_bindings_for_war_packaging -->
                <plugin>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>3.0.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.0</version>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>2.22.1</version>
                </plugin>
                <plugin>
                    <artifactId>maven-war-plugin</artifactId>
                    <version>3.2.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-install-plugin</artifactId>
                    <version>2.5.2</version>
                </plugin>
                <plugin>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <version>2.8.2</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

maven由于他的**约定大于配置**，我们之后可能遇到我们写的配置文件，无法被导出或者生效的问题， 解决方案：

```xml
<!--在build中配置resources，来防止我们资源导出失败的问题-->
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

# Servlet

## 简介

- Servlet就是sun公司开发动态web的一门技术
- Sun在这些API中提供一个接口叫做：Servlet，如果你想开发一个Servlet程序，只需要完成两个小步骤：
  - 编写一个类，实现Servlet接口
  - 把开发好的Java类部署到web服务器中

## HelloServlet

Serlvet接口Sun公司有两个默认的实现类：HttpServlet，GenericServlet

1. . 构建一个普通的Maven项目，删掉里面的src目录，以后我们的学习就在这个项目里面建立 Moudel；这个空的工程就是Maven主工程；

2. 关于Maven父子工程的理解：

   父项目中会有

   ```xml
   <modules>
   	<module>servlet-01</module>
   </modules>
   ```

   子项目会有

   ```xml
   <parent>
       <artifactId>javaweb-02-servlet</artifactId>
       <groupId>hongwei</groupId>
       <version>1.0-SNAPSHOT</version>
   </parent>
   ```

   父项目中的java子项目可以直接使用

   ```xml
   son extends father
   ```

3. Maven环境优化

   1. 修改web.xml为最新的
   2. 将maven的结构搭建完整

4. 编写一个Servlet程序

   ![image-20220721153255255](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721153255255.png)

   1. 编写一个普通类

   2. 实现Servlet接口，这里我们直接继承HttpServlet

      ```java
      public class HelloServlet extends HttpServlet {
          //由于get或者post只是请求实现的不同的方式，可以相互调用，业务逻辑都一样；
          @Override
          protected void doGet(HttpServletRequest req,
          HttpServletResponse resp) throws ServletException, IOException {
          //ServletOutputStream outputStream = resp.getOutputStream();
              PrintWriter writer = resp.getWriter(); //响应流
              writer.print("Hello,Serlvet");
          }
          @Override
          protected void doPost(HttpServletRequest req,
          HttpServletResponse resp) throws ServletException, IOException {
          	doGet(req, resp);
          }
      }
      
      ```

5.  编写Servlet的映射

   为什么需要映射：我们写的是JAVA程序，但是要通过浏览器访问，而浏览器需要连接web服务器， 所以我们需要再web服务中注册我们写的Servlet，还需给他一个浏览器能够访问的路径；

   ```xml
   <!--注册Servlet-->
   <servlet>
       <servlet-name>hello</servlet-name>
       <servlet-class>com.kuang.servlet.HelloServlet</servlet-class>
   </servlet>
   <!--Servlet的请求路径-->
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello</url-pattern>
   </servlet-mapping>
   ```

6.  配置Tomcat

7. 启动测试，OK！

## Servlet原理

Servlet是由Web服务器调用，web服务器在收到浏览器请求之后，会：

![image-20220721163321240](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721163321240.png)

## Mapping问题

1. 一个Servlet可以指定一个映射路径

   ```xml
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello</url-pattern>
   </servlet-mapping>
   ```

2. 一个Servlet可以指定多个映射路径

   ```xml
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello</url-pattern>
   </servlet-mapping>
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello2</url-pattern>
   </servlet-mapping>
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello3</url-pattern>
   </servlet-mapping>
   ```

3. 一个Servlet可以指定通用映射路径

   ```xml
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/hello/*</url-pattern>
   </servlet-mapping>
   ```

4.  默认请求路径

   ```xml
   <!--默认请求路径-->
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>/*</url-pattern>
   </servlet-mapping>
   ```

5. 指定一些后缀或者前缀等等….

   ```xml
   <!--可以自定义后缀实现请求映射
   注意点，*前面不能加项目映射的路径
   hello/sajdlkajda.action
   -->
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>*.action</url-pattern>
   </servlet-mapping>
   ```

6. 优先级问题

   指定了固有的映射路径优先级最高，如果找不到就会走默认的处理请求；

   ```xml
   <!--404-->
   <servlet>
       <servlet-name>error</servlet-name>
       <servlet-class>com.kuang.servlet.ErrorServlet</servlet-class>
   </servlet>
   <servlet-mapping>
       <servlet-name>error</servlet-name>
       <url-pattern>/*</url-pattern>
   </servlet-mapping>
   ```

## ServletContext

web容器在启动的时候，它会为每个web程序都创建一个对应的ServletContext对象，它代表了当前的 web应用；

### 共享数据

我在这个Servlet中保存的数据，可以在另外一个servlet中拿到；

```java
//赋值
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = this.getServletContext();
        String username = "宏伟";
        servletContext.setAttribute("username",username);


        PrintWriter writer = resp.getWriter();
        writer.print("Helllo,servlet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}

//获取
public class GetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = this.getServletContext();
        String username = (String) servletContext.getAttribute("username");

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        PrintWriter writer = resp.getWriter();
        writer.print(username);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}

```

### 获取初始化参数

```xml
<!--配置一些web应用初始化参数-->
<context-param>
    <param-name>url</param-name>
    <param-value>jdbc:mysql://localhost:3306/mybatis</param-value>
</context-param>
```

```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext context = this.getServletContext();
    String url = context.getInitParameter("url");
    resp.getWriter().print(url);
}
```

### 请求转发

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext context = this.getServletContext();
    System.out.println("进入了ServletDemo04");
    //RequestDispatcher requestDispatcher =
    context.getRequestDispatcher("/gp"); //转发的请求路径
    //requestDispatcher.forward(req,resp); //调用forward实现请求转发；
    context.getRequestDispatcher("/gp").forward(req,resp);
}
```

### 读取资源文件

Properties

- 在java目录下新建properties
- 在resources目录下新建properties

发现：都被打包到了同一个路径下：classes，我们俗称这个路径为classpath: 

思路：需要一个文件流；

```xml
#db.properties
username=root12312
password=zxczxczxc
```

```java
@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream resourceAsStream = this.getServletContext().getResourceAsStream("/WEB-INF/classes/db.properties");

        Properties properties = new Properties();
        properties.load(resourceAsStream);
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        resp.getWriter().print("username:"+username+",password:"+password);
    }
```

## HttpServletResponse

web服务器接收到客户端的http请求，针对这个请求，分别创建一个代表请求的HttpServletRequest对 象，代表响应的一个HttpServletResponse；

- 如果要获取客户端请求过来的参数：找HttpServletRequest
- 如果要给客户端响应一些信息：找HttpServletResponse

### 简单分类

负责向浏览器发送数据的方法

```java
ServletOutputStream getOutputStream() throws IOException;
PrintWriter getWriter() throws IOException;
```

负责向浏览器发送响应头的方法

```java
void setCharacterEncoding(String var1);
void setContentLength(int var1);
void setContentLengthLong(long var1);
void setContentType(String var1);
void setDateHeader(String var1, long var2);
void addDateHeader(String var1, long var2);
void setHeader(String var1, String var2);
void addHeader(String var1, String var2);
void setIntHeader(String var1, int var2);
void addIntHeader(String var1, int var2);
```

响应的状态码

```java
200： 成功，请求数据通过响应报文的entity-body部分发送;OK
301： 请求的URL指向的资源已经被删除；但在响应报文中通过首部Location指明了资源现               在所处的新位置；Moved Permanently
302： 响应报文Location指明资源临时新位置 Moved Temporarily
304： 客户端发出了条件式请求，但服务器上的资源未曾发生改变，则通过响应此响应状态             码通知客户端；Not Modified
307:   浏览器内部重定向
401： 需要输入账号和密码认证方能访问资源；Unauthorized
403： 请求被禁止；Forbidden
404： 服务器无法找到客户端请求的资源；Not Found
500： 服务器内部错误；Internal Server Error
502： 代理服务器从后端服务器收到了一条伪响应，如无法连接到网关；Bad Gateway
503： 服务不可用，临时服务器维护或过载，服务器无法处理请求
504： 网关超时
```

### 下载文件

1. 向浏览器输出消息 
2. 下载文件
   1. 要获取下载文件的路径
   2. 下载的文件名是啥？
   3. 设置想办法让浏览器能够支持下载我们需要的东西
   4.  获取下载文件的输入流
   5. 创建缓冲区
   6. 获取OutputStream对象
   7. 将FileOutputStream流写入到buffer缓冲区,使用OutputStream将缓冲区中的数据输出到客户端！

```
@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1. 要获取下载文件的路径
        String realPath = "G:\\宏伟.png";
        System.out.println("文件下载的路径："+realPath);
        //2. 下载的文件名是啥？
        String name = realPath.substring(realPath.lastIndexOf(File.separator)+1);
        System.out.println("文件名："+name);
        //3. 设置想办法让浏览器能够支持下载我们需要的东西
        resp.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(name,"utf-8"));
        //4.  获取下载文件的输入流
        FileInputStream fileInputStream = new FileInputStream(realPath);
        //5. 创建缓冲区
        int length = 0;
        byte[] buffer = new byte[1024];
        //6. 获取OutputStream对象
        ServletOutputStream outputStream = resp.getOutputStream();
        //7. 将FileOutputStream流写入到buffer缓冲区,使用OutputStream将缓冲区中的数据输出到客户端！
        while ((length = fileInputStream.read(buffer)) > 0){
            outputStream.write(buffer,0,length);
        }
        fileInputStream.close();
        outputStream.close();
    }
```

### 验证码功能

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //如何让浏览器3秒自动刷新一次;
    resp.setHeader("refresh","3");
    //在内存中创建一个图片
    BufferedImage image = new BufferedImage(80,20,BufferedImage.TYPE_INT_RGB);
    //得到图片
    Graphics2D g = (Graphics2D) image.getGraphics(); //笔
    //设置图片的背景颜色
    g.setColor(Color.white);
    g.fillRect(0,0,80,20);
    //给图片写数据
    g.setColor(Color.BLUE);
    g.setFont(new Font(null,Font.BOLD,20));
    g.drawString(makeNum(),0,20);
    //告诉浏览器，这个请求用图片的方式打开
    resp.setContentType("image/jpeg");
    //网站存在缓存，不让浏览器缓存
    resp.setDateHeader("expires",-1);
    resp.setHeader("Cache-Control","no-cache");
    resp.setHeader("Pragma","no-cache");
    //把图片写给浏览器
    ImageIO.write(image,"jpg", resp.getOutputStream());
}

//生成随机数
private String makeNum(){
    Random random = new Random();
    String num = random.nextInt(9999999) + "";
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 7-num.length() ; i++) {
    	sb.append("0");
    }
    num = sb.toString() + num;
    return num;
}
```

### 实现重定向

![image-20220721182246020](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721182246020.png)

B一个web资源收到客户端A请求后，B他会通知A客户端去访问另外一个web资源C，这个过程叫重定向 

常见场景：

```java
//用户登录
void sendRedirect(String var1) throws IOException;

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    /*
    resp.setHeader("Location","/r/img");
    resp.setStatus(302);
    */
    resp.sendRedirect("/r/img");//重定向
}
```

面试题：请你聊聊重定向和转发的区别？

相同点 

- 页面都会实现跳转

不同点 

- 请求转发的时候，url不会产生变化 
- 重定向时候，url地址栏会发生变化

## HttpServletRequest

HttpServletRequest代表客户端的请求，用户通过Http协议访问服务器，HTTP请求中的所有信息会被封 装到HttpServletRequest，通过这个HttpServletRequest的方法，获得客户端的所有信息；

```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("utf-8");
    resp.setCharacterEncoding("utf-8");
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String[] hobbys = req.getParameterValues("hobbys");
    System.out.println("=============================");
    //后台接收中文乱码问题
    System.out.println(username);
    System.out.println(password);
    System.out.println(Arrays.toString(hobbys));
    System.out.println("=============================");
    System.out.println(req.getContextPath());
    //通过请求转发
    //这里的 / 代表当前的web应用
    req.getRequestDispatcher("/success.jsp").forward(req,resp);
}
```

# Cookie、Session

- cookie：客户端技术 （响应，请求） 

- session：服务器技术，利用这个技术，可以保存用户的会话信息？ 我们可以把信息或者数据放在Session 中！

## Cookie

![image-20220721182842945](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721182842945.png)

1. 从请求中拿到cookie信息
2. 服务器响应给客户端cookie

```java
Cookie[] cookies = req.getCookies(); //获得Cookie
cookie.getName(); //获得cookie中的key
cookie.getValue(); //获得cookie中的vlaue
new Cookie("lastLoginTime", System.currentTimeMillis()+""); //新建一个cookie
cookie.setMaxAge(24*60*60); //设置cookie的有效期
resp.addCookie(cookie); //响应给客户端一个cookie
```

cookie：一般会保存在本地的 用户目录下 appdata；

- 一个Cookie只能保存一个信息； 
- 一个web站点可以给浏览器发送多个cookie，最多存放20个cookie；
- Cookie大小有限制4kb； 
- 300个cookie浏览器上限

**删除Cookie**

- 不设置有效期，关闭浏览器，自动失效；
- 设置有效期时间为 0 ；

**编码解码：**

```java
URLEncoder.encode("宏伟","utf-8")
URLDecoder.decode(cookie.getValue(),"UTF-8")
```

## Session（重点）

![image-20220721183307433](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721183307433.png)

- 服务器会给每一个用户（浏览器）创建一个Seesion对象；
- 一个Seesion独占一个浏览器，只要浏览器没有关闭，这个Session就存在；
- 用户登录之后，整个网站它都可以访问！--> 保存用户的信息；保存购物车的信息…..

![image-20220721183403356](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721183403356.png)

Session和cookie的区别：

- Cookie是把用户的数据写给用户的浏览器，浏览器保存 （可以保存多个）
- Session把用户的数据写到用户独占Session中，服务器端保存 （保存重要的信息，减少服务器资源的浪费）
- Session对象由服务创建；

**会话自动过期：web.xml配置**

```java
<!--设置Session默认的失效时间-->
<session-config>
    <!--15分钟后Session自动失效，以分钟为单位-->
    <session-timeout>15</session-timeout>
</session-config>
```

# MVC三层架构

Model view Controller 模型、视图、控制器

![image-20220721184030032](G:\Agogogo2022\project\studyNotes\notes\伟\JavaWeb\pictures\image-20220721184030032.png)

Model

- 业务处理 ：业务逻辑（Service）
- 数据持久层：CRUD （Dao）

View

- 展示数据
- 提供链接发起Servlet请求 （a，form，img…）

Controller （Servlet）

- 接收用户的请求 ：（req：请求参数、Session信息….）
- 交给业务层处理对应的代码
- 控制视图的跳转

# Filter （重点）

Filter：过滤器 ，用来过滤网站的数据；

- 处理中文乱码
- 登录验证….

示例

1. 实现Filter接口，重写对应的方法即可

   ```java
   public class CharacterEncodingFilter implements Filter {
       //初始化：web服务器启动，就以及初始化了，随时等待过滤对象出现！
       public void init(FilterConfig filterConfig) throws ServletException {
           System.out.println("CharacterEncodingFilter初始化");
       }
       //Chain : 链
       /*
       1. 过滤中的所有代码，在过滤特定请求的时候都会执行
       2. 必须要让过滤器继续同行
       chain.doFilter(request,response);
       */
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
           request.setCharacterEncoding("utf-8");
           response.setCharacterEncoding("utf-8");
           response.setContentType("text/html;charset=UTF-8");
           System.out.println("CharacterEncodingFilter执行前....");
           chain.doFilter(request,response); //让我们的请求继续走，如果不写，程序到这里就被拦截停止！
           System.out.println("CharacterEncodingFilter执行后....");
       }
       //销毁：web服务器关闭的时候，过滤会销毁
       public void destroy() {
       	System.out.println("CharacterEncodingFilter销毁");
       }
   }
   
   ```

2. 在web.xml中配置 Filter

   ```xml
   <filter>
       <filter-name>CharacterEncodingFilter</filter-name>
       <filter-class>com.filter.CharacterEncodingFilter</filterclass>
   </filter>
   <filter-mapping>
       <filter-name>CharacterEncodingFilter</filter-name>
       <!--只要是 /servlet的任何请求，会经过这个过滤器-->
       <url-pattern>/servlet/*</url-pattern>
       <!--<url-pattern>/*</url-pattern>-->
   </filter-mapping>
   ```

# 监听器

```java
//统计网站在线人数 ： 统计session
public class OnlineCountListener implements HttpSessionListener {
    //创建session监听： 看你的一举一动
    //一旦创建Session就会触发一次这个事件！
    public void sessionCreated(HttpSessionEvent se) {
        ServletContext ctx = se.getSession().getServletContext();
        System.out.println(se.getSession().getId());
        Integer onlineCount = (Integer) ctx.getAttribute("OnlineCount");
        if (onlineCount==null){
        	onlineCount = new Integer(1);
        }else {
            int count = onlineCount.intValue();
            onlineCount = new Integer(count+1);
        }
        ctx.setAttribute("OnlineCount",onlineCount);
    }
    //销毁session监听
    //一旦销毁Session就会触发一次这个事件！
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext ctx = se.getSession().getServletContext();
        Integer onlineCount = (Integer) ctx.getAttribute("OnlineCount");
        if (onlineCount==null){
        	onlineCount = new Integer(0);
        }else {
            int count = onlineCount.intValue();
            onlineCount = new Integer(count-1);
        }
        ctx.setAttribute("OnlineCount",onlineCount);
    }
    /*
    Session销毁：
    1. 手动销毁 getSession().invalidate();
    2. 自动销毁
    */
}
```

web.xml中注册监听器

```xml
<!--注册监听器-->
<listener>
	<listener-class>com.kuang.listener.OnlineCountListener</listenerclass>
</listener>
```

