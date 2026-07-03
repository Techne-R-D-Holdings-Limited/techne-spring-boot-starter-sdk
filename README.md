# techne-spring-boot-starter 使用说明

`techne-spring-boot-starter` 是一个基于 **Spring Boot 3** 的自动装配 starter 库（非可运行应用），集成了鉴权、限流、统一响应、全局异常、MyBatis-Plus、Redis 缓存、接口文档、国际化、短信、对象存储等一批企业级开发常用能力，引入后即可快速搭建项目骨架。

> 历史说明：本库由 takeshi 项目迁移而来，根包、类名、配置前缀均已统一为 `techne` / `com.techne.boot`；部分类的 `@author` 仍保留原作者署名。

---

## 目录

- [环境要求](#环境要求)
- [安装引入](#安装引入)
- [快速开始](#快速开始)
- [配置总览](#配置总览)
- [功能详解](#功能详解)
  - [统一响应结构](#1-统一响应结构)
  - [全局异常处理](#2-全局异常处理)
  - [Sa-Token 鉴权与请求拦截](#3-sa-token-鉴权与请求拦截)
  - [接口安全：签名 / RSA / AES](#4-接口安全签名--rsa--aes)
  - [防重复提交与 IP 限流](#5-防重复提交与-ip-限流)
  - [接口操作日志 @TechneLog](#6-接口操作日志-technelog)
  - [参数校验注解](#7-参数校验注解)
  - [字段序列化注解](#8-字段序列化注解)
  - [MyBatis-Plus 集成](#9-mybatis-plus-集成)
  - [Redis 与缓存](#10-redis-与缓存)
  - [短信发送（SPI）](#11-短信发送spi)
  - [工具类](#12-工具类)
  - [OpenAPI / Knife4j 接口文档](#13-openapi--knife4j-接口文档)
  - [国际化 i18n](#14-国际化-i18n)
  - [线程池、异步与优雅停机](#15-线程池异步与优雅停机)
- [可选依赖对照表](#可选依赖对照表)
- [注意事项与常见问题](#注意事项与常见问题)
- [构建与发布](#构建与发布)

---

## 环境要求

| 项目 | 要求 |
|------|------|
| JDK | 17+ |
| Spring Boot | 3.4.x（由本库 BOM 统一管理） |
| Redis | **必需**。Sa-Token 会话、限流、防重复提交、RSA 密钥存储、`@Cacheable` 缓存均依赖 Redisson |
| 数据库 | 可选。使用 MyBatis-Plus 相关能力时需要（默认按 MySQL 配置分页方言） |

## 安装引入

```xml
<dependency>
    <groupId>com.techne.boot</groupId>
    <artifactId>techne-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

本库**默认不传递** `spring-boot-starter-web`（为可选依赖），Web 项目需自行引入：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

引入本库后将**自动传递**以下核心依赖（无需重复声明）：

- `spring-boot-starter-aop` / `spring-boot-starter-validation` / `spring-boot-starter-actuator`
- `redisson-spring-boot-starter`（Redis 客户端）
- `mybatis-plus-spring-boot3-starter` + `mybatis-plus-jsqlparser`
- `sa-token-spring-boot3-starter` + `sa-token-redisson-spring-boot-starter`（鉴权）
- `springdoc-openapi-starter-webmvc-ui`（接口文档）
- `hutool-all`、`gson`、`libphonenumber`、`tika-core`、`jts-core`、`spring-retry`、`micrometer-tracing-bridge-brave` 等

AWS S3、Twilio、Firebase、PDF 生成等为**可选功能**，需要下游自行引入对应依赖，详见[可选依赖对照表](#可选依赖对照表)。

## 快速开始

### 1. 最小配置

`application.yml`：

```yaml
spring:
  application:
    name: my-app            # 必填：用于日志路径、token 名称、Redis key 前缀等
  profiles:
    include: techne          # 引入本库内置的 application-techne.yml 默认配置
  data:
    redis:
      host: 127.0.0.1
      port: 6379

techne:
  project-name: my-app       # 项目名：用于 AES/RSA 密钥派生、S3 桶名等
```

> `include: techne` 会加载本库内置的 [application-techne.yml](src/main/resources/application-techne.yml)，其中包含 MVC、文件上传大小、优雅停机、日志滚动、MyBatis-Plus 逻辑删除、Sa-Token 等默认值。**注意配置优先级**：被 include 的 profile 配置优先级高于主 `application.yml`，若要覆盖其中的值，需要再 include 一个自己的 profile 配置文件（见[注意事项](#注意事项与常见问题)）。

### 2. 编写第一个接口

```java
@RestController
@RequestMapping("/user")
public class UserController extends AbstractBasicController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public ResponseData<Void> add(@Validated @RequestBody UserDTO dto) {
        return retBool(userService.save(dto));
    }

    @GetMapping("/info")
    public ResponseData<UserVO> info(Long id) {
        return retData(userService.getVoById(id));
    }
}
```

启动后即自动获得：统一响应包装、全局异常处理、请求/响应日志打印、Sa-Token 鉴权拦截、traceId 链路追踪、Swagger 文档等能力。

### 3. 自动装配说明

本库**没有手写** `spring.factories` / `AutoConfiguration.imports`，全部由编译期注解处理器（mica-auto）扫描 `@AutoConfiguration` 等注解自动生成到 `META-INF/spring/` 下。自动装配的主要组件包括：

| 组件 | 说明 |
|------|------|
| `TechneConfig` | 核心配置：CORS、国际化 MessageSource、ObjectMapper、Sa-Token、Redisson、CacheManager、事务管理器；并开启 `@EnableCaching`、`@EnableRetry`、`@EnableScheduling` |
| `StaticConfig` | 启动监听器：初始化全局静态变量（配置、AES/RSA 密钥、应用名、运行环境） |
| `ThreadPoolConfig` | 线程池与 `@EnableAsync` 异步支持（集成链路追踪上下文透传） |
| `TechneFilter` | Servlet 过滤器：请求体缓存、客户端 IP 解析、请求/响应日志、traceId |
| `TechneInterceptor`（经 `TechneSaTokenConfig` 注册） | 鉴权、签名校验、限流、防重复提交 |
| `GlobalExceptionHandler` | 全局异常统一转 `ResponseData` |
| `MybatisPlusConfig` / `DefaultMetaObjectHandler` / `TechneSqlInjector` | MyBatis-Plus 分页插件、字段自动填充、自定义 SQL 注入器 |
| `OpenApiConfig` | springdoc / knife4j 接口文档 |
| `AwsConfig` / `AwsSecretsManagerConfig` | AWS S3 / Secrets Manager（仅 classpath 存在对应 SDK 时生效） |
| `RedisComponent` / `TechneAsyncComponent` / `ShutdownManager` | Redis 操作组件、异步日志组件、优雅停机管理 |

---

## 配置总览

### `techne.*` 主配置（`TechneProperties`）

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `techne.project-name` | String | — | 项目名称，用于 RSA/AES 密钥派生、S3 桶命名等，建议必填 |
| `techne.app-platform` | boolean | `false` | 是否开启移动端请求工具限制（限制非移动端 User-Agent 访问） |
| `techne.include-error-field-name` | boolean | `true` | 参数校验失败提示中是否包含字段名 |
| `techne.max-executor-close-timeout` | long | `30` | 优雅停机时定时任务线程池关闭最大等待秒数 |
| `techne.exclude-url` | String[] | — | 额外排除的 URL（不进入 Filter / 拦截器 / Sa-Token 逻辑） |
| `techne.aes-key` | String | 自动派生 | AES 密钥，**必须 16 位**；不配置时由「项目名+环境」派生 |
| `techne.open-ip-blacklist` | boolean | `false` | 开启 IP 黑名单：超过限流阈值的 IP 封禁 24 小时 |
| `techne.enable-request-param-log` | boolean | `true` | 是否打印请求参数日志 |
| `techne.enable-response-data-log` | boolean | `true` | 是否打印响应数据日志 |
| `techne.redisson-cache-config-location` | String | — | `@Cacheable` 使用的 Redisson 缓存配置文件路径 |
| `techne.sql-log-filter.enabled` | boolean | `false` | 是否启用 SQL 日志屏蔽（配合 MDC 标记使用） |
| `techne.sql-log-filter.extra-packages` | List | 空 | 额外需要屏蔽 SQL 日志的 logger 包前缀 |

### 第三方集成配置

以下配置均支持「明文配置」或「AWS Secrets Manager 密钥名」两种方式（`*-secrets` 后缀的配置项优先，从 Secrets Manager 取值）：

| 前缀 | 配置类 | 主要配置项 | 用途 |
|------|--------|-----------|------|
| `techne.aws-secrets` | `AWSSecretsManagerCredentials` | `access-key`、`secret-key`、`secret-id`、`bucket-name`、`region`（默认 `us-west-2`）、`bucket-accelerate`、`file-acl` 等 | AWS Secrets Manager / S3 |
| `techne.twilio` | `TwilioProperties` | `account-sid`、`auth-token`、`messaging-service-sid` | Twilio 短信 |
| `techne.sms-broadcast` | `SmsBroadcastProperties` | `user-name`、`password`、`from` | SMS Broadcast 短信 |
| `techne.firebase` | `FirebaseCredentials` | `json-file-name`（默认 `firebase.json`）、`database-url` | Firebase 推送/实时库 |
| `techne.mandrill` | `MandrillCredentials` | `api-key`、`from-email`、`from-name` | Mandrill 邮件 |

### 内置默认配置（`application-techne.yml` 摘要）

通过 `spring.profiles.include: techne` 启用，关键默认值：

- 文件上传：单文件/单请求最大 **50MB**
- `server.shutdown: graceful`，停机阶段超时 30s
- 日志：写入 `logs/${spring.application.name}/`，单文件 30MB 滚动，保留 30 天（本库自带 `logback-spring.xml`）
- MyBatis-Plus 逻辑删除：字段 `deleteTime`，删除值 `utc_timestamp(3)`，未删除值 `0`
- Sa-Token：token 名 `${spring.application.name}-satoken`、永不过期（30 天无操作过期）、uuid 风格、从 Header 读写

---

## 功能详解

### 1. 统一响应结构

所有接口统一返回 `ResponseData<T>`（`basic.pojo.com.technehq.boot.ResponseData`）：

```json
{
  "code": 200,
  "message": "成功",
  "data": { "id": 1, "name": "John" },
  "metadata": null,
  "time": "2026-06-11T10:30:00.000Z",
  "traceId": "abc123def456"
}
```

- `code` / `message`：状态码与提示语，提示语自动按请求方语言（`Accept-Language`）做国际化
- `data`：业务数据；`metadata`：附加元数据（如分页信息）
- `traceId`：链路追踪 ID，自动从 MDC 获取，方便日志排查

**构建方式**——继承 `AbstractBasicController` 后直接调用便捷方法，或使用 `ResponseData` 静态工厂：

```java
return success();                          // code=200
return fail();                             // code=1000
return fail("自定义消息或i18n键");
return retData(userVO);                    // 带数据成功
return retData(TechneCode.SIGN_ERROR);     // 使用预定义状态
return retBool(updated);                   // true→成功，false→失败
```

**状态码体系**（定义于 `TechneCode` 接口，类型为 `RetBO(code, i18nKey)`）：

| 区间 | 含义 | 示例 |
|------|------|------|
| 200 | 成功 | `SUCCESS` |
| 1000–1999 | 系统/通用错误 | `FAIL(1000)`、`PARAMETER_ERROR(1001)`、`DB_ERROR(1003)`、`REPEAT_SUBMIT(1006)` |
| 2000–2999 | 数据/验证类错误 | 验证码过期、数据已存在等 |
| 3000–3999 | 账号类错误 | 账号不存在、被禁用等 |
| 4000–4999 | 安全/限制类错误 | 签名错误、`RATE_LIMIT(4003)` 限流等 |
| 5000–5999 | 认证/授权错误 | `NOT_TOKEN(5000)`、token 过期、无角色/权限等 |
| 6000–6999 | 文件类错误 | 文件为空、类型错误 |

业务项目可仿照 `RetBO` 自定义自己的状态码常量接口。

### 2. 全局异常处理

`GlobalExceptionHandler` 自动捕获并转换为 `ResponseData`，无需业务代码 try-catch：

| 异常 | 返回 code |
|------|----------|
| `TechneException`（业务异常） | 异常内携带的 code |
| 参数校验失败（`MethodArgumentNotValidException` 等） | 1001 |
| SQL / 数据访问异常 | 1003 |
| Redis 异常 | 1004 |
| 空指针 | 1002 |
| Sa-Token 未登录（`NotLoginException`） | 5000–5005 |
| 签名错误（`SaSignException`） | 4001 |
| 无角色 / 无权限 | 5006 / 5007 |

**业务异常**抛 `TechneException` 即可：

```java
// 直接抛出预定义状态
throw new TechneException(TechneCode.ACCOUNT_DOES_NOT_EXIST);

// 自定义 code + 消息（支持 i18n key 与 MessageFormat 参数）
throw new TechneException(3001, "user.not.found", new Object[]{userId});

// 配合 Optional
userOpt.orElseThrow(TechneException.supplier(TechneCode.NOT_EXIST));
```

另提供函数式工具 `Either<L, R>`（`exception.com.technehq.boot.Either`），用于在 Stream/Lambda 中优雅地处理受检异常。

### 3. Sa-Token 鉴权与请求拦截

本库整合 [Sa-Token](https://sa-token.cc/)（含 Redisson 持久化），登录、踢人、权限校验等直接使用 Sa-Token 原生 API（`StpUtil.login(id)` 等）。

**接入方式**：实现 `TechneSaTokenConfig` 接口并注册为 Bean，在 `saRouteBuild()` 中返回 `TechneInterceptor`，即可启用路由拦截鉴权：

```java
@Configuration
public class SaTokenConfig implements TechneSaTokenConfig {

    @Override
    public TechneInterceptor saRouteBuild() {
        // 无参：默认执行注解鉴权（@SaCheckLogin / @SaCheckRole / @SaCheckPermission）
        // 也可传入自定义认证函数，编写自己的路由匹配与鉴权逻辑
        return new TechneInterceptor();
    }
}
```

`TechneInterceptor.preHandle()` 在每个请求上依次执行：

1. **请求日志记录**（方法、参数，自动脱敏 password 等字段）
2. **客户端平台校验**（`techne.app-platform=true` 时限制非移动端）
3. **参数签名校验**（见下节）
4. **IP 限流 / 防重复提交**（`@RepeatSubmit` 注解触发）
5. **登录/角色/权限鉴权**

同时 `TechneSaTokenConfig` 还自动注册了一批参数格式化器（手机号、金额、排序字段、枚举转换等，见[字段序列化注解](#8-字段序列化注解)）。

### 4. 接口安全：签名 / RSA / AES

#### 控制注解 `@SystemSecurity`

标注在 Controller 方法（或类）上，细粒度控制安全校验：

| 属性 | 默认 | 说明 |
|------|------|------|
| `passToken` | `false` | 跳过 token 校验 |
| `passPlatform` | `false` | 跳过移动端平台校验 |
| `passSignature` | `false` | 跳过参数签名校验 |
| `passTimestamp` | `true` | 跳过客户端时间戳校验 |
| `passAll` | `false` | 跳过全部校验（优先级最高） |
| `inDecode` | `false` | 请求体需要 RSA 解密 |
| `outEncode` | `false` | 响应 `data` 字段需要 RSA 加密 |

#### 参数签名（防篡改 / 防重放）

配置 `sa-token.sign.secret-key` 后启用。客户端需在请求头携带：

| Header | 说明 |
|--------|------|
| `timestamp` | 请求时间戳（毫秒），超出允许偏差则拒绝 |
| `nonce` | 一次性随机串，重复使用则拒绝（防重放） |
| `sign` | 签名值：`md5(排序后的参数串 + "&key=" + secretKey)`（由 `TechneSaSignTemplate` 实现，参数包含 URL 参数与 JSON Body） |

签名校验失败返回 code `4001`。

#### RSA 请求解密 / 响应加密

```java
@SystemSecurity(inDecode = true, outEncode = true)
@PostMapping("/sensitive")
public ResponseData<String> sensitive(@RequestBody SensitiveDTO dto) {
    // dto 已被 DecodeRequestBodyAdvice 自动解密
    // 返回值 data 字段会被 EncodeResponseBodyAdvice 自动加密
    return retData(dto.getName());
}
```

RSA 密钥对在应用首次启动时生成并存入 Redis（分布式锁保证集群只生成一次），前端使用公钥加密上送、公钥解密响应。业务代码可用 `TechneUtil.decryptByPrivateKey(data)` 手动解密。

#### AES

`StaticConfig` 启动时初始化全局 AES 实例（密钥取 `techne.aes-key`，未配置则自动派生），业务代码与 `AesCiphertextTypeHandler` / `PasswordTypeHandler`（数据库字段加密）共用。

### 5. 防重复提交与 IP 限流

使用 `@RepeatSubmit` 注解（基于 Redisson RateLimiter）：

```java
// 防重复提交：1 秒内相同 IP+方法+URL+登录者+参数 的请求视为重复
@RepeatSubmit(rateIntervalMillis = 1000)
@PostMapping("/order")
public ResponseData<Void> createOrder(@RequestBody OrderDTO dto) { ... }

// IP 限流：单 IP 60 秒内最多 10 次
@RepeatSubmit(ipRate = 10, ipRateIntervalMillis = 60_000)
@PostMapping("/captcha")
public ResponseData<Void> sendCaptcha() { ... }
```

| 属性 | 默认 | 说明 |
|------|------|------|
| `rateIntervalMillis` | `0` | 防重复提交间隔毫秒数，>0 才生效；key 为 IP+Method+URL+LoginId+参数的 MD5 |
| `exclusionFieldName` | — | 不参与重复判定的字段 |
| `msg` | 重复提交提示 | 自定义提示语（支持 i18n key） |
| `ipRate` / `ipRateIntervalMillis` | `10` / `0` | IP 限流速率与时间窗，`ipRateIntervalMillis>0` 才生效 |

配置 `techne.open-ip-blacklist: true` 后，超限 IP 自动加入黑名单封禁 24 小时。

### 6. 接口操作日志 @TechneLog

标注 `@TechneLog` 的接口，请求完成后由 `TechneAsyncComponent` **异步**写入数据库 `tb_sys_log` 表（需自行建表，字段对应实体 `TbSysLog`：日志类型、登录 ID、客户端 IP、UserAgent、请求头/参数、响应数据、traceId、耗时、成功标志等）。

```java
@TechneLog(logType = LogTypeEnum.LOGIN, exclusionFieldName = {"captcha"})
@PostMapping("/login")
public ResponseData<TokenVO> login(@RequestBody LoginDTO dto) { ... }
```

`LogTypeEnum` 支持：`INSERT/DELETE/UPDATE/SELECT/IMPORT/EXPORT/UPLOAD/DOWNLOAD/LOGIN/REGISTER/LOGOUT/PAY/VERIFY/OAUTH/CALLBACK/OTHER`。password 类字段自动排除，不会落库。

另外 `TechneFilter` 默认对所有请求打印参数与响应日志（可用 `techne.enable-request-param-log` / `techne.enable-response-data-log` 关闭）。

**SQL 日志屏蔽**：批量任务等场景下不想刷 SQL 日志时，开启 `techne.sql-log-filter.enabled: true`，在代码块中设置 MDC 标记即可屏蔽 MyBatis/JDBC 等 SQL 日志（由 Logback TurboFilter `TechneSqlDenyTurboFilter` 实现），额外包名通过 `extra-packages` 追加。

### 7. 参数校验注解

位于 `com.techne.boot.constraints`，配合 `@Validated` 使用，消息默认走 i18n：

| 注解 | 功能 | 示例 |
|------|------|------|
| `@VerifyPhoneNumber` | 全球手机号校验（libphonenumber），`defaultRegion` 指定默认区号 | `@VerifyPhoneNumber(defaultRegion = "CN") private String phone;` |
| `@VerifyVersion` | 版本号格式 `x.x.x` | `@VerifyVersion private String version;` |
| `@VerifyNumber` | 数字枚举值校验 | `@VerifyNumber({0, 1, 2}) private Integer status;` |
| `@VerifyString` | 字符串枚举值校验（忽略大小写） | `@VerifyString({"male", "female"}) private String gender;` |
| `@VerifyNumberDigits` | 整数位/小数位位数校验 | `@VerifyNumberDigits(maxInteger = 10, maxFraction = 2) private BigDecimal amount;` |
| `@VerifySortColumn` | 排序字段防 SQL 注入（MyBatis-Plus `SqlInjectionUtils`） | 标注在排序字段参数上 |

> 所有校验器对 `null` / 空串放行，需要必填请叠加 `@NotNull` / `@NotBlank`。

### 8. 字段序列化注解

位于 `com.techne.boot.annotation`，同时支持 JSON Body（Jackson）与 URL/表单参数（FormatterFactory，已由 `TechneSaTokenConfig` 自动注册）：

| 注解 | 功能 |
|------|------|
| `@CurrencyConversion` | 货币单位转换：入参元→分（×100），出参分→元（÷100，按 `pattern` 格式化，默认 `0.00`）。数据库存分、前端展示元的标准做法 |
| `@BigDecimalFormat` | `BigDecimal` 出入参格式化，`pattern` 默认 `0.00` |
| `@NumZeroFormat` | 去除数字字符串前导零 |

其他内置序列化行为：

- `TechneInstantSerializer`（`@JsonComponent` 全局注册）：`Instant` 统一格式化输出（ISO-8601，毫秒 3 位）
- `@VerifyPhoneNumber` 同时触发 `PhoneNumberDeserializer`，自动把手机号规整为 E.164 格式
- `StringToEnumConverterFactory`：URL 参数字符串自动转枚举（支持 `@JsonValue` 值匹配）
- Gson 侧由 `GsonUtil` 提供统一实例（注册了 `Instant`/`LocalDateTime`/`ZonedDateTime`/`Year`/`YearMonth` 等全套时间适配器）：
  ```java
  Gson gson = GsonUtil.gson();                 // 标准实例
  Gson g2 = GsonUtil.gsonLongToString();       // Long 序列化为 String（防前端精度丢失）
  Gson g3 = GsonUtil.gsonIncludeNull();        // 序列化包含 null
  ```

### 9. MyBatis-Plus 集成

自动装配内容（`MybatisPlusConfig`）：

- **分页插件**：`PaginationInnerInterceptor`（默认 MySQL 方言）
- **字段自动填充**（`DefaultMetaObjectHandler`）：插入时填充 `createTime`/`updateTime`，更新时填充 `updateTime`（`Instant.now()`）
- **逻辑删除**：默认字段 `deleteTime`，删除值 `utc_timestamp(3)`、未删除值 `0`（来自 `application-techne.yml`）
- **SQL 注入器**（`TechneSqlInjector`）：注入 `SelectIncludeDelById`（按 ID 查询且包含已逻辑删除记录）

**推荐用法**——实体继承基类、Mapper/Service 使用增强接口：

```java
@Data
@TableName(value = "tb_user", autoResultMap = true)
public class User extends AbstractBasicEntity {   // 自带 createTime/updateTime（Instant）
    private Long userId;
    @TableField(typeHandler = AesCiphertextTypeHandler.class)
    private String idCard;                        // 数据库 AES 加密存储
}

public interface UserMapper extends TechneMapper<User> { }      // 40+ 增强方法
public interface UserService extends ITechneService<User> { }
```

**分页**——入参继承 `BasicPage` 系列，配合 `TechnePage`：

| 入参基类 | 字段 |
|----------|------|
| `BasicPage` | `pageNum`、`pageSize`（-1 不分页）、`keyword` |
| `BasicSortPage` | + `sortColumn`（自动驼峰转下划线、防注入）、`sortAsc` |
| `BasicQueryPage` | + `startTime`、`endTime` |
| `BasicSortQueryPage` | 以上全部 |

```java
@GetMapping("/page")
public ResponseData<TechnePage<UserVO>> page(@Validated BasicSortQueryPage query) {
    TechnePage<User> page = userMapper.selectPage(TechnePage.of(query), wrapper);
    return retData(page.convert(UserVO.class));   // 泛型转换
}
```

**内置 TypeHandler**（`com.techne.boot.mybatisplus.typehandler`，使用时实体需 `@TableName(autoResultMap = true)`）：

| TypeHandler | Java 类型 ↔ 数据库 | 说明 |
|-------------|---------------------|------|
| `TechneInstantTypeHandler` | `Instant` ↔ TIMESTAMP(3) | 已自动注册，毫秒 3 位 |
| `GeoPointTypeHandler` | `GeoPointBO` ↔ GEOMETRY/POINT | 已自动注册，WKB 经纬度 |
| `LocaleTypeHandler` / `ZoneIdTypeHandler` | `Locale`/`ZoneId` ↔ VARCHAR | 已自动注册 |
| `AesCiphertextTypeHandler` | String ↔ VARCHAR | AES 加密存、解密读 |
| `PasswordTypeHandler` | String ↔ VARCHAR | 仅加密存（密码） |
| `InetAddressTypeHandler` | String ↔ BINARY | IP 存字节数组 |
| `AmazonS3TypeHandler` | String ↔ VARCHAR | 存 S3 key，读出自动转预签名 URL |
| `String/Integer/LongAbstractListTypeHandler` | `List<T>` ↔ VARCHAR | 逗号分隔字符串 |

### 10. Redis 与缓存

- **`RedisComponent`**（自动装配）：封装 String/Hash/List/Set/ZSet/Geo 全类型操作，并提供 `expireAtEndOfDay/Week/Month/Year`、`saveToEndOfDay` 等到期便捷方法。
- **Redisson**：`RedissonClient` 已自动装配，可直接注入使用分布式锁、限流器等。
- **Spring Cache**：`@EnableCaching` 已开启，CacheManager 支持**在 cacheName 中内联 TTL**（`#` 后为秒数）：

```java
@Cacheable(value = "user#3600", key = "#id")   // 缓存 1 小时
public UserVO getUser(Long id) { ... }
```

- **Redis Key 规范**：实现 `TechneRedisKeyFormat` 接口的枚举（参考 `TechneRedisKeyEnum`）统一管理 key 模板，`moduleKey(params)` / `projectKey(params)` 自动拼接应用名前缀。

### 11. 短信发送（SPI）

短信能力通过 SPI 解耦，接口为 `SmsInterface`：

```java
void sendMessage(boolean send, String phoneNumber, String message);
```

- **内置实现**（通过 `@AutoService` 自动注册 SPI）：
  - `TwilioImpl`：需引入 `com.twilio.sdk:twilio` 并配置 `techne.twilio.*`
  - `SmsBroadcastImpl`：HTTP 调用 SMS Broadcast，配置 `techne.sms-broadcast.*`
- **调用方式**：`SmsUtil.sendMessage(send, phoneNumber, message)`（内部经 `SmsFactory` 取第一个可用实现）
- **自定义实现**：实现 `SmsInterface` 并标注 `@AutoService(SmsInterface.class)`（编译期自动生成 `META-INF/services` 注册文件）即可被加载。

### 12. 工具类

位于 `com.techne.boot.util`，全部为静态方法风格。标 ⚠ 的依赖可选库，**需下游自行引入对应依赖**：

| 工具类 | 功能 | 依赖 |
|--------|------|------|
| `TechneUtil` | 综合工具：客户端 IP、IP 归属地、i18n 消息格式化、Lambda 取属性/列名、元分转换、经纬度距离（Haversine）、Apple/Facebook OAuth 验证、RSA 解密、Tika 文件类型识别等 | 内置 |
| `GsonUtil` | 统一 Gson 实例与 JSON 互转 | 内置 |
| `ZonedDateTimeUtil` | 带时区时间工具：各时间段起止（日/周/月/年）、时间差、范围列表等 | 内置 |
| `InstantUtil` | Instant 时间范围生成 | 内置 |
| `GlobalPhoneNumberUtil` | 全球手机号解析/校验/E.164 格式化 | 内置 |
| `CollectorsUtil` + `BigDecimalSummaryStatistics` | Stream 中 BigDecimal 求和/最值/平均/汇总统计 | 内置 |
| `TechneThreadUtil` | 线程睡眠、线程池优雅关闭 | 内置 |
| `AmazonS3Util` | S3 上传（缩略图/视频时长/压缩）、下载、预签名 URL、删除 | ⚠ `software.amazon.awssdk:s3-transfer-manager` |
| `AwsSecretsManagerUtil` | 读取 AWS Secrets Manager 密钥 | ⚠ `software.amazon.awssdk:secretsmanager` |
| `FirebaseUtil` | Firebase 实时数据库读写 + FCM 消息推送（单播/多播/透传） | ⚠ `com.google.firebase:firebase-admin` |
| `MandrillUtil` | Mandrill 邮件：模板、附件、内嵌图片，链式构建 | ⚠ `com.mandrillapp.wrapper.lutung:lutung` |
| `PdfUtil` | Thymeleaf HTML 模板 → PDF（预览/下载/保存） | ⚠ `com.itextpdf:html2pdf` + `spring-boot-starter-thymeleaf` |
| `FrameConverterUtil` | 视频/GIF 取首帧缩略图、取时长 | ⚠ `org.bytedeco:javacv` + `org.bytedeco:ffmpeg`（平台分类器） |

### 13. OpenAPI / Knife4j 接口文档

`OpenApiConfig` 自动装配 springdoc，文档页面统一访问 `/doc.html`（也可用 springdoc 原生的 `/swagger-ui.html`）。

`/doc.html` 支持两种 UI，按 classpath 自动切换，无需配置：

- **引入了 knife4j**：使用 knife4j 的增强 UI（其 `doc.html` 位于 `META-INF/resources/`，静态资源查找顺序优先于本库的 `static/`，自动生效）；
- **未引入 knife4j**：回落到本库内置的 RapiDoc 页面（`static/doc.html`）。注意该页面从公网 CDN（jsdelivr）加载脚本，**纯内网环境无法使用**，此时请引入 knife4j。

- 默认开启；关闭：`springdoc.api-docs.enabled: false`
- **接口分组/多版本文档**：方法上标注 `@ApiGroup({"1.0", "1.1"})`，按组生成文档
- `@ApiSupport(author = "xxx")`：标注接口作者
- 启用 knife4j 增强 UI 需自行引入 `com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter`（可选依赖）

### 14. 国际化 i18n

- 资源文件位于本库 `techne-i18n/`：`messages.properties`（默认）、`messages_zh_CN`、`messages_en_US`，`MessageSource` 已自动装配。
- 响应消息、校验提示、异常提示均按请求头 `Accept-Language` 自动选择语言。
- 消息支持 `MessageFormat` 占位参数：`fail("user.not.found", new Object[]{userId})`。
- 业务项目可提供自己的 messages 资源扩展词条；`TechneUtil.formatMessage(message, args)` 可手动格式化。

**约定请求头**（`RequestConstants` 定义）：

| Header | 用途 |
|--------|------|
| `Accept-Language` | 语言 |
| `Accept-Version` | 接口版本 |
| `timezone` | 客户端时区（如 `Asia/Shanghai`） |
| `geo-point` | 客户端经纬度 JSON `{"lon":..,"lat":..}` |
| `timestamp` / `nonce` / `sign` | 签名三件套（见安全章节） |

### 15. 线程池、异步与优雅停机

- `ThreadPoolConfig` 装配 `ThreadPoolTaskExecutor` 与 `ScheduledExecutorService`，并开启 `@EnableAsync`；TaskDecorator 自动透传链路追踪上下文（traceId 在异步线程中不丢失）。
- `@EnableScheduling`、`@EnableRetry`（spring-retry）已默认开启。
- 优雅停机：`server.shutdown: graceful` + `ShutdownManager` 在 `@PreDestroy` 时安全关闭线程池，最大等待时间由 `techne.max-executor-close-timeout` 控制。

---

## 可选依赖对照表

以下功能对应的依赖在本库中为 `optional`，**不会传递**给下游，使用对应功能前必须在自己的 pom 中显式引入（AWS 体系版本由传递的 BOM 管理，无需写版本号）：

| 功能 | 需要引入的依赖 |
|------|----------------|
| Web MVC（几乎必选） | `org.springframework.boot:spring-boot-starter-web` |
| S3 文件存储（`AmazonS3Util`、`AmazonS3TypeHandler`、`AwsConfig`） | `software.amazon.awssdk:s3-transfer-manager` |
| AWS Secrets Manager（`AwsSecretsManagerUtil`、各 `*-secrets` 配置） | `software.amazon.awssdk:secretsmanager` |
| Twilio 短信（`TwilioImpl`） | `com.twilio.sdk:twilio` |
| Firebase 推送/实时库（`FirebaseUtil`） | `com.google.firebase:firebase-admin` |
| Mandrill 邮件（`MandrillUtil`） | `com.mandrillapp.wrapper.lutung:lutung` |
| HTML 转 PDF（`PdfUtil`） | `com.itextpdf:html2pdf` + `spring-boot-starter-thymeleaf` |
| 视频/GIF 处理（`FrameConverterUtil`、S3 上传缩略图） | `org.bytedeco:javacv` + `org.bytedeco:ffmpeg`（带平台分类器，如 `ffmpeg-platform`） |
| Knife4j 文档增强 UI | `com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter` |
| 模板引擎（PDF/邮件模板） | `org.springframework.boot:spring-boot-starter-thymeleaf` |

---

## 注意事项与常见问题

1. **Redis 是硬依赖**。未配置可用的 Redis 连接时应用无法正常启动（Sa-Token 会话、RSA 密钥、限流等均存于 Redis）。
2. **配置覆盖优先级**：`spring.profiles.include: techne` 引入的内置配置优先级**高于**主 `application.yml`。若要覆盖其中的值（如上传大小、Sa-Token 参数），需要把覆盖值写进另一个 profile 文件并在 `techne` **之后** include，例如：
   ```yaml
   spring:
     profiles:
       include: techne,override   # application-override.yml 中写覆盖值
   ```
3. **`techne.aes-key` 必须 16 位**，否则启动校验失败；不配置则自动按「项目名+环境」派生（同一项目同一环境密钥稳定）。
4. **本库自带 `logback-spring.xml`**，会接管日志输出（控制台 + 滚动文件）。如需完全自定义日志，在自己项目中提供同名文件覆盖即可。
5. **`@TechneLog` 需要建表**：异步日志写入 `tb_sys_log`，表结构参照实体 `basic.pojo.com.technehq.boot.TbSysLog` 自行创建；不建表则不要使用该注解。
6. **逻辑删除字段约定**：默认 `deleteTime`（删除时写入 `utc_timestamp(3)`，未删除为 `0`），实体需有对应字段；不适用时在自己的配置中覆盖 `mybatis-plus.global-config.db-config.*`。
7. **历史署名**：本库由 takeshi 项目迁移而来，类名与配置前缀均已统一为 `techne`，仅部分类的 `@author` 注释仍保留原作者署名。
8. **改动自动装配注解后必须重新编译**（`mvn clean compile`），`META-INF` 下的装配文件由注解处理器在编译期生成。
9. **Windows 控制台中文乱码**：`mvn` 输出在 GBK 控制台可能乱码，不影响构建结果。

---

## 构建与发布

```bash
mvn clean compile        # 编译（最快验证）
mvn clean package        # 打包：main / sources / javadoc 三个 jar
mvn clean install        # 安装到本地仓库，供其他项目引用
mvn -Prelease clean deploy   # 签名并发布到 Maven Central（需 GPG 与 Central Portal 凭据）
```

发布前置条件：

- `~/.m2/settings.xml` 中配置 `<server><id>central</id>` 的 Sonatype Central Portal token；
- 本机可用 GPG 私钥（passphrase 可通过 `-Dgpg.passphrase=` 传入）。

---

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
