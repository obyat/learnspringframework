# Spring fundamentals: IoC, beans, and dependency injection

This guide explains the runnable `Game` example in this repository. Read it while stepping through the code in your IDE.

## 1. Spring Framework vs. Spring Boot

| Term | Meaning |
| --- | --- |
| Spring Framework | A large Java framework ecosystem. Spring Core supplies dependency injection, while other projects add web, data, security, messaging, and more. |
| Spring Boot | An opinionated layer on top of Spring. It supplies sensible defaults, starter dependencies, auto-configuration, embedded-server support, and production features. |
| Spring Boot starter | A curated dependency set, such as `spring-boot-starter-web` for REST APIs or `spring-boot-starter-data-jpa` for JPA persistence. |
| Auto-configuration | Boot examines the classpath and configuration, then registers sensible infrastructure beans. It "backs off" when you provide your own bean of the same purpose. |

Spring Boot does not replace Spring Framework. It makes starting and operating Spring applications faster.

## 2. Core vocabulary

| Term | Plain-English definition |
| --- | --- |
| Inversion of Control (IoC) | Instead of your code creating and wiring every object, Spring controls object creation and wiring. |
| IoC container / `ApplicationContext` | The Spring object that stores bean definitions, creates beans, injects dependencies, applies configuration, publishes events, and manages lifecycle callbacks. |
| Bean | An object created and managed by the Spring container. It is not merely any Java object. |
| Bean definition | Metadata that tells Spring how to create a bean. The container output in this project lists bean definition names. |
| Dependency Injection (DI) | The way Spring supplies an object's required collaborators. `GameRunner` receives a `Game` instead of creating a `MarioGame` itself. |
| Component scanning | Spring searches packages for annotated classes such as `@Component`, then registers them as bean definitions. |
| Autowiring | Spring resolves a dependency, usually by type, and injects the matching bean. |

## 3. The flow in this project

`LearnSpringFrameworkApplication` is in the root package, `com.in28minutes.spring.learn_spring_framework`. Because of `@SpringBootApplication`, Spring scans that package and its children.

```text
1. Java invokes main().
2. SpringApplication.run(...) creates an ApplicationContext.
3. Component scanning finds @Component classes.
4. Spring registers MarioGame, PacMan, SuperContraGame, and GameRunner as bean definitions.
5. Spring creates GameRunner and resolves its `@Autowired` Game field.
6. Three Game implementations match, so SuperContraGame is selected because it is `@Primary`.
7. The example retrieves GameRunner from the container and calls run().
```

This is IoC: `main` does not create or wire a Game into GameRunner. Spring owns that wiring.

### Why `GameRunner` depends on `Game`

The current `GameRunner` uses field injection to demonstrate that style. It still depends on the `Game` interface rather than a concrete implementation. The constructor-injected version below is the preferred production design:

```java
public class GameRunner {
    private final Game game;

    public GameRunner(Game game) {
        this.game = game;
    }
}
```

`GameRunner` depends on the interface, not on `MarioGame`. That makes the class less coupled and easier to test. A test can pass a fake `Game`; a configuration can choose a different real implementation.

`MarioGame`, `PacMan`, and `SuperContraGame` are all beans because they have `@Component`. `SuperContraGame` is the default candidate because it also has `@Primary`; a `@Qualifier` can choose a specific alternative.

## 4. `@SpringBootApplication`

`@SpringBootApplication` combines three annotations:

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

You normally use the combined annotation on the application entry-point class. `@SpringBootConfiguration` is Boot's configuration-class annotation and is a Boot-specific alternative to `@Configuration`.

- `@SpringBootConfiguration` identifies the primary Boot configuration class and lets it declare or import bean definitions.
- `@EnableAutoConfiguration` lets Boot configure common infrastructure based on dependencies and properties.
- `@ComponentScan` finds components in the application package and all child packages.

Keep the application class high in your package hierarchy. Putting it too deep can cause Spring to miss components in sibling packages.

## 5. `@Component` and stereotype annotations

`@Component` tells component scanning that a class should be a Spring bean.

```java
@Component
public class MarioGame implements Game {
    // ...
}
```

Spring creates singleton-scoped beans by default: one shared instance per `ApplicationContext`.

These annotations are specialized forms of `@Component`:

| Annotation | Typical use |
| --- | --- |
| `@Component` | Generic Spring-managed class. |
| `@Service` | Service/business-logic class. |
| `@Repository` | Data-access class; it is eligible for persistence exception translation when the relevant infrastructure is configured. |
| `@Controller` | MVC controller returning a view. |
| `@RestController` | REST controller; return values are written to the HTTP response body. |

Use the annotation that communicates your class's role. It helps reviewers understand your design.

## 6. Dependency injection styles

### Constructor injection - use this by default

```java
@Component
class GameRunner {
    private final Game game;

    GameRunner(Game game) {
        this.game = game;
    }
}
```

Why it is preferred:

- Required dependencies are visible in the constructor.
- `final` prevents required dependencies from being changed later.
- The class is easy to unit test with a fake or mock dependency.
- A Spring bean cannot exist half-configured.

When a class has exactly one constructor, Spring uses it automatically. You do **not** need `@Autowired` on that constructor.

Use `@Autowired` on a constructor only when a class has multiple constructors and you need to make Spring's choice explicit. Prefer designing the class with one clear construction path instead.

### Field injection - avoid for required dependencies

```java
// Avoid for normal application code.
@Autowired
private Game game;
```

Field injection hides the dependency, prevents a required field from being cleanly `final`, and makes plain unit tests more awkward. Do not mix it with constructor injection for the same dependency.

### Setter injection - use only for genuinely optional dependencies

```java
@Component
class Scoreboard {
    private Game game;

    @Autowired(required = false)
    void setGame(Game game) {
        this.game = game;
    }
}
```

Setter injection makes sense when the dependency is optional or legitimately reconfigurable. Most service dependencies should not be optional, so constructor injection remains the default.

## 7. What happens with multiple implementations?

This project already has three beans of type `Game`: `MarioGame`, `PacMan`, and `SuperContraGame`. `SuperContraGame` is `@Primary`, so it is selected by default. If you remove `@Primary` without adding a `@Qualifier`, Spring cannot safely guess which Game to inject and startup fails with a `NoUniqueBeanDefinitionException`.

### Option A: choose a default with `@Primary`

```java
@Component
@Primary
class SuperContraGame implements Game {
    // default Game implementation
}
```

`@Primary` chooses the default when no other preference is supplied.

### Option B: choose explicitly with `@Qualifier`

```java
@Component("pacMan")
class PacMan implements Game {
    // ...
}

@Component
class GameRunner {
    private final Game game;

    GameRunner(@Qualifier("pacMan") Game game) {
        this.game = game;
    }
}
```

`@Qualifier` is explicit and is often clearer when a class truly needs one particular implementation. Prefer meaningful qualifier names and avoid using bean names as a hidden service locator.

The snippets in this section are illustrative. Do not add them to this project until you deliberately want to experiment with multiple `Game` beans.

## 8. Registering beans with `@Configuration` and `@Bean`

Use component scanning for classes you own and can annotate. Use `@Bean` when configuring a third-party class, applying custom construction logic, or wiring conditionally.

```java
@Configuration
class GameConfiguration {

    @Bean
    Game game() {
        return new PacMan();
    }
}
```

The method name becomes the default bean name. Spring calls this method, stores the returned object, and can inject it where a `Game` is needed.

Do not register the same concern twice. For example, do not use both `@Component` on `PacMan` and a `@Bean` method that returns a `PacMan` unless you intentionally need two separate beans.

## 9. Bean scope and lifecycle

| Scope | Meaning | Typical use |
| --- | --- | --- |
| `singleton` | One instance per Spring container; the default. | Stateless services, repositories, configuration. |
| `prototype` | A new instance each time it is requested. | Rare; create only when lifecycle and cleanup are understood. |
| `request` | One instance per HTTP request. | Web-specific request state. |
| `session` | One instance per HTTP session. | Rare; avoid storing server-side session state when possible. |

Lifecycle hooks exist, but use them thoughtfully:

```java
@PostConstruct
void initialize() {
    // Runs after dependency injection.
}

@PreDestroy
void cleanUp() {
    // Runs when a singleton bean is being destroyed.
}
```

Prefer explicit startup runners, managed clients, and clean resource ownership over using lifecycle hooks for business logic.

## 10. Configuration, profiles, and secrets

`application.properties` contains configuration for this application. Spring Boot reads properties/YAML files, environment variables, and command-line arguments out of the box. Kubernetes ConfigMaps, AWS Parameter Store, and AWS Secrets Manager can also supply configuration when you add and configure the appropriate Spring Cloud or AWS integration.

Use `@ConfigurationProperties` for grouped configuration instead of scattering `@Value` across a codebase:

```java
@ConfigurationProperties(prefix = "app")
public record AppProperties(Duration timeout, URI baseUrl) {
}
```

Enable it with `@ConfigurationPropertiesScan` on the application class or `@EnableConfigurationProperties(AppProperties.class)` in a configuration class.

Profiles select environment-specific configuration:

```properties
# application-dev.properties
app.base-url=http://localhost:8080
```

On macOS or Linux, run:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

On PowerShell, run:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
.\mvnw.cmd spring-boot:run
```

Never hard-code credentials, API keys, or database passwords in source control. Supply them through a secret-management system and give the application only the IAM permissions it needs.

## 11. Testing levels

The existing `@SpringBootTest` verifies that the full `ApplicationContext` can start. By default, its web environment is a mock environment; it does not start a real HTTP server unless you configure one, for example with `webEnvironment = RANDOM_PORT`. It is valuable but comparatively slower than a plain unit test.

| Test type | Purpose |
| --- | --- |
| Plain unit test | Test one class with fakes/mocks; fastest and most focused. |
| Slice test | Test one Spring layer, such as `@WebMvcTest` for controllers or `@DataJpaTest` for JPA. |
| `@SpringBootTest` | Start the full context to verify integration and configuration. |
| Integration/contract test | Test real infrastructure boundaries or API contracts where practical. |

Aim for many unit tests, focused slice tests, and fewer full-context tests.

## 12. Exercises

1. Before running the app, predict why `SuperContraGame` is selected for `GameRunner`.
2. Temporarily remove `@Primary` from `SuperContraGame`, run the app, and read the ambiguity error.
3. Restore `@Primary`, then add a `@Qualifier` at the injection point to select a non-default Game. Compare the trade-offs.
4. Create a constructor-injected GameRunner variation, replace its Game with a test fake, and unit-test it without starting Spring.
5. Create a `@Configuration` class with a `@Bean` method for an object you cannot annotate.
6. Add an `application-dev.properties` file and activate the `dev` profile.
7. Add Spring Boot Actuator later and explain the difference between liveness and readiness probes.

## 13. Interview-quality explanations

Practice saying these answers aloud:

> **What is IoC?** IoC means the framework controls object construction and wiring. In Spring, the `ApplicationContext` manages beans and supplies their dependencies, so application classes focus on behavior rather than assembly.

> **Why constructor injection?** It makes required dependencies explicit, supports immutability, prevents invalid partially initialized objects, and makes unit testing easy. With one constructor, Spring injects it automatically.

> **What is the difference between `@Component` and `@Bean`?** `@Component` registers a class through scanning. `@Bean` registers the object returned by a configuration method and is useful for third-party classes or custom construction.

> **What does Spring Boot auto-configuration do?** It registers conventional infrastructure based on dependencies and properties, while backing off when the application defines its own compatible bean.
