# Learn Spring Framework

This repository is a small, runnable Spring Boot project designed to teach Spring Core concepts before moving into REST APIs, databases, microservices, and AWS.

It currently demonstrates:

- Spring Boot application startup
- The Spring IoC container (`ApplicationContext`)
- Component scanning and beans
- Constructor, setter, and field dependency-injection examples
- Programming to an interface (`Game`) rather than a concrete class (`MarioGame`)
- A full-context Spring Boot test

## Spring Core and Boot: interview quick reference

Use this pattern when answering an interview question: define the concept, explain
what Spring does, name the trade-off, and tie it to a small example. The detailed
[Spring fundamentals](docs/01-spring-fundamentals.md) guide goes deeper; this
section is designed to be practiced aloud.

### Code map in this repository

- [GameRunner](src/main/java/com/in28minutes/spring/learn_spring_framework/game/GameRunner.java)
  demonstrates **field injection**.
- [MyWebController](src/main/java/com/in28minutes/spring/learn_spring_framework/enterprise/example/web/MyWebController.java)
  demonstrates **constructor injection**, the preferred choice for required
  dependencies.
- [BusinessService](src/main/java/com/in28minutes/spring/learn_spring_framework/enterprise/example/business/BusinessService.java)
  demonstrates **setter injection**.
- [SuperContraGame](src/main/java/com/in28minutes/spring/learn_spring_framework/game/SuperContraGame.java)
  is the default `Game` because it is marked `@Primary`.

### IoC, `ApplicationContext`, and beans ✅

**Inversion of Control (IoC)** means Spring controls object creation, configuration,
and wiring instead of application code doing it with `new`. **Dependency Injection
(DI)** is the usual way Spring implements IoC: a class declares what it needs, and
the container supplies it.

A **bean** is an object created and lifecycle-managed by Spring. An object created
with `new` is an ordinary Java object, not a Spring bean. The
**`ApplicationContext`** is Spring's IoC container: it holds bean definitions,
creates beans, resolves dependencies, manages lifecycle callbacks, and adds
features such as events, messages, and resource loading.

```java
ConfigurableApplicationContext context =
        SpringApplication.run(LearnSpringFrameworkApplication.class, args);

GameRunner runner = context.getBean(GameRunner.class); // Useful for this tutorial.
```

In normal application classes, do not call `getBean(...)`. Declare the dependency
instead and let Spring inject it.

> **Interview answer:** "IoC moves object creation and wiring into the
> `ApplicationContext`. DI is the mechanism through which Spring supplies a bean's
> collaborators, which reduces coupling and improves testability."

### Dependency Injection and its styles ✅

Spring usually resolves a dependency **by type**. For example, a class that depends
on `Game` can receive any registered implementation of `Game`.

#### Constructor injection - the default for required dependencies

```java
@Component
class MyWebController {
    private final BusinessService businessService;

    MyWebController(BusinessService businessService) {
        this.businessService = businessService;
    }
}
```

Use constructor injection for required collaborators because dependencies are
explicit, can be `final`, and are easy to provide in a plain unit test. If a class
has exactly one constructor, `@Autowired` is optional; Spring uses that constructor
automatically.

#### Field injection versus constructor injection

```java
// Field injection: works only when GameRunner itself is Spring-managed.
@Autowired
private Game game;
```

| Field injection | Constructor injection |
| --- | --- |
| Concise for a small demonstration. | Explicitly states required dependencies. |
| The dependency is hidden and cannot be `final`. | Supports immutable fields. |
| Plain unit tests need reflection or a Spring context. | A test can call `new MyWebController(fakeService)`. |
| A manually created object can have a `null` field. | The object cannot be created without required dependencies. |

`@Autowired` requests injection; it does **not** make the target type a bean. The
containing class and its dependency must both be registered with Spring.

#### Setter injection - for optional or reconfigurable dependencies

```java
@Autowired
void setAuditService(AuditService auditService) {
    this.auditService = auditService;
}
```

Setter injection is useful when a dependency is genuinely optional or can change.
Avoid it for required business dependencies because the object can exist before the
setter has run.

> **Interview answer:** "I default to constructor injection for required
> dependencies. Field injection works, but it hides dependencies and makes testing
> and immutability worse. I reserve setter injection for optional configuration."

### `@Component`, stereotypes, and `@ComponentScan` ✅

`@Component` marks a class for discovery during component scanning. The following
stereotype annotations are specialized forms of `@Component` and communicate a
class's role:

```java
@Service        // Business logic
class PaymentService { }

@Repository     // Data access; also participates in persistence exception translation
class PaymentRepository { }

@RestController // HTTP API endpoints
class PaymentController { }
```

`@ComponentScan` tells Spring where to look for those classes:

```java
@Configuration
@ComponentScan(basePackages = "com.example.payment")
class PaymentConfiguration { }
```

In a Boot application, `@SpringBootApplication` performs a component scan from its
own package through child packages. Put the application class in a common root
package. Use an explicit scan only when you intentionally need another package:

```java
@SpringBootApplication(scanBasePackages = {
        "com.example.app", "com.example.shared"
})
class Application { }
```

> **Interview answer:** "Component scanning discovers my annotated application
> classes. It is different from Boot auto-configuration, which conditionally adds
> framework infrastructure based on the classpath and configuration."

### `@Configuration` and `@Bean` ✅

Use `@Configuration` for a class that declares bean definitions. Use `@Bean` on a
factory method when you need custom construction or need to register a third-party
class that you cannot annotate.

```java
@Configuration
class TimeConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ReportService reportService(Clock clock) {
        return new ReportService(clock);
    }
}
```

Spring manages each returned object; the method name is the default bean name.
Method parameters are dependencies resolved by the container. Prefer component
scanning for application classes you own and `@Bean` for external classes, factory
logic, or deliberate configuration.

> **Interview answer:** "`@Component` registers a class through scanning. `@Bean`
> registers the object returned by a method, so it is ideal for a library class or
> custom setup that I cannot express with `@Component`."

### `@Primary` and `@Qualifier` ✅

When more than one bean matches an injection type, Spring needs help choosing one.
Without a choice, startup normally fails with `NoUniqueBeanDefinitionException`.

`@Primary` declares the default candidate. This repository has three `Game`
implementations, and `SuperContraGame` is selected by default:

```java
@Component
@Primary
class SuperContraGame implements Game {
    // Default Game implementation
}
```

`@Qualifier` selects a specific candidate at one injection point, even if another
candidate is primary:

```java
@Component
class GameRunner {
    GameRunner(@Qualifier("marioGame") Game game) {
        // Explicitly select the bean named marioGame.
    }
}
```

Use `@Primary` for an application-wide sensible default. Use `@Qualifier` when one
consumer specifically needs a particular implementation.

> **Interview answer:** "`@Primary` is a default preference; `@Qualifier` is an
> explicit choice at the injection point. If neither resolves the ambiguity, Spring
> fails fast rather than guessing."

### Spring Boot configuration annotations ✅

```text
@SpringBootApplication
  |-- @SpringBootConfiguration
  |     `-- Boot's configuration-class variant of @Configuration
  |-- @EnableAutoConfiguration
  |     `-- conditionally configures Spring/third-party infrastructure
  `-- @ComponentScan
        `-- discovers annotated application components
```

**`@SpringBootConfiguration`** is Boot's configuration-class annotation. It is a
Boot-specific alternative to `@Configuration` that helps Boot tests find the
primary application configuration. You normally receive it indirectly through
`@SpringBootApplication`, rather than placing it on every configuration class.

**`@EnableAutoConfiguration`** asks Boot to configure likely infrastructure based
on the classpath, properties, and beans you already define. For example, adding
`spring-boot-starter-webmvc` allows Boot to configure MVC and an embedded server;
your `@RestController` is still found by component scanning. Auto-configuration is
conditional and usually backs off when you provide your own bean.

**`@SpringBootApplication`** is the usual application entry-point annotation. It
combines the three annotations above and exposes options such as
`scanBasePackages` and auto-configuration exclusions.

The explicit form below is conceptually equivalent, but the combined annotation is
what you normally use:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
class ApplicationConfiguration { }
```

```java
@SpringBootApplication
public class LearnSpringFrameworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(LearnSpringFrameworkApplication.class, args);
    }
}
```

### Spring Boot startup basics ✅

When `main` calls `SpringApplication.run(...)`, the high-level flow is:

1. Boot creates and refreshes an appropriate `ApplicationContext` and prepares the
   application environment.
2. Spring processes the main configuration class, component scan, and applicable
   conditional auto-configurations.
3. Component scanning registers this project's beans, including `GameRunner`,
   `MarioGame`, `PacMan`, and `SuperContraGame`.
4. Spring resolves dependencies and creates required singleton beans. `GameRunner`
   receives `SuperContraGame` for its `Game` field because `SuperContraGame` is
   `@Primary`.
5. Boot finishes refreshing the context and returns it from `run(...)`. This
   learning application then retrieves `GameRunner` and runs it.

> **Interview answer:** "`SpringApplication.run` bootstraps and refreshes the
> `ApplicationContext`. During startup, Spring processes configuration, scans
> components, evaluates conditional auto-configuration, resolves the dependency
> graph, and creates the required beans."

### Short interview drill

Practice these answers without reading the notes:

1. Why is constructor injection usually preferred to field injection?
2. What is the difference between component scanning and auto-configuration?
3. When would you choose `@Bean` instead of `@Component`?
4. How do `@Primary` and `@Qualifier` resolve multiple beans of the same type?
5. What does `@SpringBootApplication` combine, and why should it sit in a root
   package?

For every answer, name the trade-off and point to the matching example in this
repository. That turns a definition into an interview-quality explanation.

## Spring Framework modules

Spring Framework is modular: an application can use only the parts it needs. This
project begins with the **Core Container**, where Spring provides configuration,
beans, and dependency injection.

- **Core Container** - `spring-core`, `spring-beans`, `spring-context`, and
  `spring-expression`; provides the IoC container and dependency injection.
- **Data Access and Integration** - JDBC, transactions, ORM integration, and
  messaging integrations such as JMS.
- **Web** - Spring MVC for Servlet-based web applications and Spring WebFlux for
  reactive web applications.
- **AOP** - aspect-oriented programming for cross-cutting concerns such as logging,
  security, and transactions.
- **Messaging** - support for application messaging and WebSocket-based messaging.
- **Testing** - `spring-test`, which provides Spring-aware testing support,
  including test contexts and web-layer test utilities.

Spring Boot starter dependencies select and configure the modules commonly needed
for a particular type of application.

## Spring ecosystem projects

Spring Framework provides the foundation. Other Spring projects build on it for
specific application needs. The examples below are illustrative; they do not add
dependencies to this project.

### [Spring Boot](https://spring.io/projects/spring-boot/)

Spring Boot creates stand-alone, production-ready Spring applications with starter
dependencies, auto-configuration, embedded servers, health checks, and metrics.

**Example:** Add `spring-boot-starter-webmvc` to build a REST API, then create a
controller:

```java
@RestController
class GreetingController {
    @GetMapping("/greeting")
    String greeting() {
        return "Hello, Spring";
    }
}
```

### [Spring Data](https://spring.io/projects/spring-data/)

Spring Data provides consistent data-access abstractions for stores such as JPA,
MongoDB, Redis, and JDBC/R2DBC.

**Example:** With Spring Data JPA, a repository interface can provide CRUD methods
without writing the implementation:

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
```

### [Spring Security](https://spring.io/projects/spring-security/)

Spring Security handles authentication, authorization, and common web-security
protections.

**Example:** Allow anonymous access to public endpoints while requiring a signed-in
user for everything else:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/public/**").permitAll()
                    .anyRequest().authenticated())
            .build();
}
```

### [Spring Cloud](https://spring.io/projects/spring-cloud/)

Spring Cloud helps build distributed systems with configuration management, service
discovery, routing, service-to-service calls, load balancing, and circuit breakers.

**Example:** An API gateway can forward all `/orders/**` requests to an order
service:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: orders
          uri: http://orders-service:8080
          predicates:
            - Path=/orders/**
```

Use a Spring Cloud release train that is compatible with your Spring Boot version.

### [Spring Batch](https://spring.io/projects/spring-batch/)

Spring Batch is for reliable, large-scale batch processing.

**Example:** A nightly job can read a CSV file, validate and transform each record,
then write the results to a database in chunks.

### [Spring Integration](https://spring.io/projects/spring-integration/)

Spring Integration supports message-driven applications and enterprise integration
patterns.

**Example:** Receive an order message from a queue, validate it, send it to a
fulfillment service, and publish a confirmation event.

Other useful projects include [Spring for Apache Kafka](https://spring.io/projects/spring-kafka/),
[Spring for GraphQL](https://spring.io/projects/spring-graphql/),
[Spring Session](https://spring.io/projects/spring-session/), and
[Spring AI](https://spring.io/projects/spring-ai/).

## Start here

1. Run the application and read the IoC-container output.
2. Read [Spring fundamentals](docs/01-spring-fundamentals.md).
3. Work through the exercises at the end of that guide.
4. Use the [Spring Boot and AWS interview guide](docs/02-spring-boot-aws-interview-guide.md) to prepare for senior Java roles.

## Run the project

### Prerequisite

This project currently targets Java 25, as configured in `pom.xml`. Install a compatible JDK before running it. Many enterprise roles use Java 17 or Java 21 LTS, so learn the concepts here while avoiding reliance on Java 25-only features unless the role explicitly requires them.

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

Run the tests with:

```powershell
.\mvnw.cmd test
```

The application deliberately prints the container type, context ID, and registered bean definitions. `logging.level.org.springframework=debug` is also enabled in `application.properties`, so Spring's own startup logs will be verbose. Change that value to `info` when you want quieter output.

## What happens when the application starts?

```text
main()
  |
  v
SpringApplication.run(...)
  |
  v
ApplicationContext (Spring's IoC container)
  |
  +-- component scan finds @Component classes
  |     +-- MarioGame  -> bean named marioGame
  |     +-- PacMan     -> bean named pacMan
  |     +-- SuperContraGame -> bean named superContraGame (@Primary)
  |     +-- GameRunner -> bean named gameRunner
  |
  +-- Spring injects SuperContraGame into GameRunner's @Autowired Game field
          because it is the default @Primary Game bean
```

The source comments and interview quick reference explain this flow in context. The
`Game` example deliberately contains multiple candidates so you can observe
`@Primary`; the `@Qualifier` example above shows how to select a non-default bean.

## Important learning rule

Use `context.getBean(GameRunner.class)` here to make the container visible while learning. In normal application classes, do not fetch dependencies from the `ApplicationContext`; declare required dependencies in the constructor and let Spring inject them.
