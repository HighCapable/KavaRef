# Third-party Loggers

> Here are some ways to integrate third-party logging frameworks for reference and use.
>
> Please read [Log Management](../library/kavaref-core.md#log-management) for usage instructions.

## SLF4J

[Project URL](https://github.com/qos-ch/slf4j)

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

KavaRef filters logs by `KavaRef.logLevel` first, then passes them to SLF4J for printing.

If you need to print DEBUG level logs, in addition to setting `KavaRef.logLevel`,
you also need to enable the DEBUG level of the logger named `KavaRef` in the SLF4J logging implementation.

:::

### Normal Project

SLF4J is only a logging facade, you need to introduce both `slf4j-api` and a logging implementation, such as `slf4j-simple` or `logback-classic`.

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("org.slf4j:slf4j-api:<version>")
// Choose a logging implementation, here is slf4j-simple as an example.
implementation("org.slf4j:slf4j-simple:<version>")
```

Please change `<version>` to the latest version of SLF4J.

Then, set it to KavaRef when your application starts.

> The following example

```kotlin
fun main(args: Array<String>) {
    KavaRef.setLogger(Slf4jLogger())
    // Your application logic.
}
```

When using `slf4j-simple`, you can enable DEBUG level logs of `KavaRef` through the following system property.

> The following example

```
-Dorg.slf4j.simpleLogger.log.KavaRef=debug
```

### Spring Boot Project

Spring Boot has integrated SLF4J and Logback through `spring-boot-starter-logging` by default, you don't need to introduce additional dependencies.

You only need to set it to KavaRef before starting the Spring application.

> The following example

```kotlin
fun main(args: Array<String>) {
    KavaRef.setLogger(Slf4jLogger())
    runApplication<MyApplication>(*args)
}
```

You can enable DEBUG level logs of `KavaRef` in `application.properties`.

> The following example

```properties
logging.level.KavaRef=debug
```