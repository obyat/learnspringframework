# Learn Spring Framework

This repository is a small, runnable Spring Boot project designed to teach Spring Core concepts before moving into REST APIs, databases, microservices, and AWS.

It currently demonstrates:

- Spring Boot application startup
- The Spring IoC container (`ApplicationContext`)
- Component scanning and beans
- Constructor, setter, and field dependency-injection examples
- Programming to an interface (`Game`) rather than a concrete class (`MarioGame`)
- A full-context Spring Boot test

## Completed Spring Core topics

- IoC ✅
- ApplicationContext ✅
- Beans ✅
- Dependency Injection ✅
- Constructor Injection ✅
- Field vs Constructor DI ✅
- `@Component` / stereotypes ✅
- `@ComponentScan` ✅
- `@Configuration` ✅
- `@Bean` ✅
- `@Qualifier` ✅
- `@Primary` ✅
- `@SpringBootConfiguration` ✅
- `@EnableAutoConfiguration` ✅
- `@SpringBootApplication` ✅
- Spring Boot startup basics ✅

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
  |     +-- GameRunner -> bean named gameRunner
  |
  +-- Spring injects MarioGame into GameRunner's @Autowired Game field
          because it is the only Game bean
```

The source comments explain this flow in context. The documentation includes additional examples such as `@Qualifier`, `@Primary`, `@Bean`, profiles, REST testing, and AWS deployment choices without adding extra beans that would change this simple example's behavior.

## Important learning rule

Use `context.getBean(GameRunner.class)` here to make the container visible while learning. In normal application classes, do not fetch dependencies from the `ApplicationContext`; declare required dependencies in the constructor and let Spring inject them.
