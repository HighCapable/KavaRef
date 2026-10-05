# 第三方日志记录器

> 这里收录了一些第三方日志框架的接入方式，可供参考与使用。
>
> 使用方法请阅读 [日志管理](../library/kavaref-core.md#日志管理)。

## SLF4J

[项目地址](https://github.com/qos-ch/slf4j)

> Simple Logging Facade for Java

```kotlin
class Slf4jLogger : KavaRefRuntime.Logger {

    override val tag = "KavaRef"

    private val logger = LoggerFactory.getLogger(tag)

    override fun debug(msg: Any?, throwable: Throwable?) {
        logger.debug(msg.toString(), throwable)
    }

    override fun info(msg: Any?, throwable: Throwable?) {
        logger.info(msg.toString(), throwable)
    }

    override fun warn(msg: Any?, throwable: Throwable?) {
        logger.warn(msg.toString(), throwable)
    }

    override fun error(msg: Any?, throwable: Throwable?) {
        logger.error(msg.toString(), throwable)
    }
}
```

::: tip

KavaRef 会先按照 `KavaRef.logLevel` 过滤日志，然后再交给 SLF4J 打印。

如果你需要打印 DEBUG 级别的日志，除了设置 `KavaRef.logLevel` 外，还需要在 SLF4J 的日志实现中启用名为 `KavaRef` 的日志记录器的 DEBUG 级别。

:::

### 普通项目

SLF4J 仅为日志门面，你需要同时引入 `slf4j-api` 与一个日志实现，例如 `slf4j-simple` 或 `logback-classic`。

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("org.slf4j:slf4j-api:<version>")
// 选择一个日志实现，这里以 slf4j-simple 为例
implementation("org.slf4j:slf4j-simple:<version>")
```

请将 `<version>` 修改为 SLF4J 的最新版本。

然后，在你的应用启动时将其设置到 KavaRef 上即可。

> 示例如下

```kotlin
fun main(args: Array<String>) {
    KavaRef.setLogger(Slf4jLogger())
    // 你的应用逻辑
}
```

使用 `slf4j-simple` 时，你可以通过以下系统属性启用 `KavaRef` 的 DEBUG 级别日志。

> 示例如下

```
-Dorg.slf4j.simpleLogger.log.KavaRef=debug
```

### Spring Boot 项目

Spring Boot 默认已经通过 `spring-boot-starter-logging` 集成了 SLF4J 与 Logback，你无需引入额外的依赖。

你只需要在启动 Spring 应用之前将其设置到 KavaRef 上即可。

> 示例如下

```kotlin
fun main(args: Array<String>) {
    KavaRef.setLogger(Slf4jLogger())
    runApplication<MyApplication>(*args)
}
```

你可以在 `application.properties` 中启用 `KavaRef` 的 DEBUG 级别日志。

> 示例如下

```properties
logging.level.KavaRef=debug
```