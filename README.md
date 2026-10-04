# Learn Spring Framework

This repository is a small, runnable Spring Boot project designed to teach Spring Core concepts before moving into REST APIs, databases, microservices, and AWS.

It currently demonstrates:

- Spring Boot application startup
- The Spring IoC container (`ApplicationContext`)
- Component scanning and beans
- Constructor dependency injection
- Programming to an interface (`Game`) rather than a concrete class (`MarioGame`)
- A full-context Spring Boot test

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
  +-- Spring calls GameRunner(Game game)
          and injects MarioGame because it is the only Game bean
```

The source comments explain this flow in context. The documentation includes additional examples such as `@Qualifier`, `@Primary`, `@Bean`, profiles, REST testing, and AWS deployment choices without adding extra beans that would change this simple example's behavior.

## Important learning rule

Use `context.getBean(GameRunner.class)` here to make the container visible while learning. In normal application classes, do not fetch dependencies from the `ApplicationContext`; declare required dependencies in the constructor and let Spring inject them.
