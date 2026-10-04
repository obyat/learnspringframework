# Spring Boot and AWS interview guide

This guide is a practical roadmap for a senior Java/Spring Boot/AWS interview. It starts with the current `Game` example and grows toward production systems and system-design conversations.

## What current Cognizant roles emphasize

Exact interview rounds and client requirements vary, so always tailor your preparation to the job description you receive. Current official Cognizant postings demonstrate recurring themes such as:

- Core Java: collections, streams/lambdas, concurrency, JVM/memory behavior, exceptions, design patterns, and performance.
- Spring: IoC/DI, Spring Boot, REST APIs, JPA/Hibernate, microservices, testing, and production troubleshooting.
- AWS: EC2, Lambda, S3, RDS, API Gateway, IAM, CloudWatch, and cloud deployment trade-offs.
- Delivery: Git, Maven/Gradle, CI/CD, observability, code reviews, Agile delivery, and client/stakeholder communication.
- Senior behavior: technical direction, mentoring, incident analysis, and explaining trade-offs clearly.

Examples of currently accessible role descriptions:

- [Senior Full Stack Engineer (Java/AWS)](https://careers.cognizant.com/us-en/jobs/00070352301/senior-full-stack-java-aws/)
- [Senior Java AWS Developer](https://careers.cognizant.com/us-en/jobs/00070218411/senior-java-aws-developer/)
- [Java Full Stack Developer](https://careers.cognizant.com/us-en/jobs/00070596591/java-full-stack-developer/)
- [Senior Java/Spring Boot/Angular/AWS Developer](https://careers.cognizant.com/us-en/jobs/00069986452/senior-full-stack-developer-java-spring-boot-angular-aws-remote/)

The AWS decision guide below also covers related services such as DynamoDB, API Gateway, SQS, and EventBridge because they are common senior-level AWS discussion topics; do not assume every client project uses every service.

## What "senior" means in an interview

A senior answer should do more than name a framework feature. It should explain:

1. The requirement and constraints.
2. The design choice.
3. The trade-off and failure mode.
4. How the system is tested, observed, secured, and operated.
5. How you would communicate the decision and help the team implement it.

For example, do not only say "use SQS." Explain why asynchronous processing helps, how consumers become idempotent, how retries work, where failures go (DLQ), and which metrics alert you when the queue grows.

## A six-week learning sequence

Assume roughly 8-12 focused hours each week. If you have less time, keep the order and reduce scope rather than skipping fundamentals.

| Week | Focus | Build or explain by the end |
| --- | --- | --- |
| 1 | Core Java + Spring Core | Explain IoC, beans, constructor injection, scopes, component scanning, `@Primary`, and `@Qualifier` using this project. |
| 2 | Spring Boot + REST | Build a small CRUD API with validation, global error handling, pagination, DTOs, and OpenAPI documentation. |
| 3 | Data + testing | Add PostgreSQL, JPA mappings, transactions, migrations, unit/slice/integration tests, and explain N+1 queries. |
| 4 | Distributed systems | Discuss REST versus events, Kafka/SQS, idempotency, retries, timeouts, circuit breakers, caching, and eventual consistency. |
| 5 | AWS + containers | Containerize the API; design an ECS/Fargate or Lambda deployment with IAM, secrets, networking, metrics, and logs. |
| 6 | System design + senior communication | Practice architecture diagrams, failure analysis, code review feedback, mentoring stories, and timed mock interviews. |

## 1. Java foundations you should be able to explain

Before a Spring interview, be comfortable with:

- OOP: encapsulation, inheritance versus composition, interfaces, SOLID, and dependency inversion.
- Collections: `List`, `Set`, `Map`, hash-based lookup, ordering, mutability, and choosing data structures based on complexity.
- Modern Java: streams, lambdas, `Optional`, records, immutability, and method references.
- Concurrency: threads, executor services, `CompletableFuture`, synchronization, race conditions, thread safety, and avoiding shared mutable state.
- JVM: heap versus stack, garbage collection at a high level, class loading, memory leaks, and reading thread/heap dumps conceptually.
- Errors: checked versus unchecked exceptions, meaningful domain exceptions, error translation, and preserving diagnostic context.

Practice questions:

- When would you use `HashMap` instead of `ConcurrentHashMap`?
- Why is immutability useful in concurrent services?
- What causes a memory leak in a garbage-collected language?
- How would you diagnose high CPU, a slow endpoint, or thread-pool exhaustion?

## 2. Spring Core and Spring Boot checklist

Use [Spring fundamentals](01-spring-fundamentals.md) first, then be ready to explain these topics:

| Topic | Strong interview answer should include |
| --- | --- |
| IoC and DI | The `ApplicationContext` owns bean lifecycle and wiring; constructor injection keeps dependencies explicit and immutable. |
| Bean resolution | Spring resolves by type. When multiple candidates exist, make the injection point unambiguous, commonly with `@Primary` or `@Qualifier`. |
| Component scanning | Spring detects stereotype annotations below the configured scan package. |
| `@Configuration` / `@Bean` | Use scanning for owned classes; use `@Bean` for third-party classes, explicit setup, or conditional wiring. |
| Auto-configuration | Boot configures sensible infrastructure from the classpath/properties and backs off when custom beans are supplied. |
| Profiles/configuration | Keep environment configuration external; use `@ConfigurationProperties`; never hard-code secrets. |
| Web API design | Resource-oriented URLs, appropriate status codes, validation, DTOs, pagination, error responses, idempotency, and versioning strategy. |
| Transactions | Define a clear transaction boundary in the service layer; understand rollback rules, isolation, propagation, and proxy/self-invocation caveats. |
| JPA performance | Avoid N+1 queries, use indexes and pagination, understand lazy/eager loading, and measure query behavior. |
| Tests | Many unit tests, focused slice tests, and a smaller number of full integration tests. |
| Operations | Actuator, health/readiness checks, structured logs, metrics, tracing, configuration, and graceful shutdown. |

### High-value Spring questions

1. Why is constructor injection better than field injection?
2. What happens when two beans implement the same interface?
3. What does `@SpringBootApplication` do?
4. How does Spring Boot decide which auto-configurations to apply?
5. What is the difference between `@Component`, `@Service`, and `@Repository`?
6. Why does `@Transactional` sometimes appear not to work when one method calls another in the same class?
7. How do you prevent N+1 queries in JPA?
8. How would you validate a REST request and return a consistent error response?
9. When would you use `@WebMvcTest`, `@DataJpaTest`, and `@SpringBootTest`?
10. How would you safely roll out a backward-incompatible database change?

Do not memorize one-sentence answers. Write a small implementation and explain what would fail in production if the design were careless.

## 3. AWS choices and trade-offs

### Compute

| Service | Good fit | Trade-offs to explain |
| --- | --- | --- |
| ECS/Fargate | Containerized Spring Boot service with predictable HTTP workloads and minimal cluster administration. | Cost/scale tuning, startup time, container image security, task sizing, and deployment strategy. |
| EKS | Teams that need Kubernetes portability or advanced platform control. | More operational complexity; do not choose it just because it is fashionable. |
| Lambda | Short-lived event-driven work, scheduled jobs, lightweight APIs, or bursty workloads. | Cold starts, execution limits, connection reuse, packaging, and unsuitable long-running processes. |
| EC2 | Specialized host control or legacy migration needs. | You own patching, capacity, availability, and more operational work. |

### Data and integration

| Need | Likely service | Explain the decision |
| --- | --- | --- |
| Relational transactions and SQL joins | RDS or Aurora PostgreSQL/MySQL | Multi-AZ, backups, connection pooling, schema migrations, indexes, and read replicas. |
| High-scale key/value or document access | DynamoDB | Partition-key design, access patterns first, eventual consistency options, hot partitions, TTL, and secondary indexes. |
| Object storage | S3 | Encryption, lifecycle policies, event notifications, signed URLs, versioning, and not treating it as a relational database. |
| Buffer work / decouple services | SQS | Standard queues are at-least-once and can be out of order. FIFO queues preserve message-group order and provide deduplication/exactly-once processing semantics. In either case, use idempotent consumers, sensible visibility timeouts, retries, DLQs, and queue-depth alarms. |
| Fan-out notifications | SNS | Producers publish once; multiple subscribers receive messages. |
| Event routing | EventBridge | Event bus and rule-based routing across services/accounts; compare with SNS/SQS based on delivery and routing needs. |
| Streaming/event log | Self-managed Apache Kafka or Amazon MSK (managed Kafka) | Ordered partitions, consumer groups, retention, replay, schema evolution, and operating complexity. |

### Edge, security, and operations

| Concern | What to say |
| --- | --- |
| HTTP entry point | Use an ALB for container services or API Gateway for managed API concerns; choose based on workload and features, not habit. |
| IAM | Use roles and least privilege. Applications should receive temporary credentials, not long-lived access keys in configuration files. |
| Secrets | Use Secrets Manager for secrets and, where appropriate, configured rotation. Use Parameter Store for configuration or SecureString values with rotation handled by your process. Restrict access by role. |
| Networking | Place workloads in appropriate VPC subnets, restrict inbound traffic with security groups, and keep databases private. |
| Observability | Emit structured logs, metrics, and traces; use correlation IDs; alert on user-impacting signals such as error rate, latency, saturation, and queue depth. |
| Resilience | Multi-AZ where needed, health checks, auto scaling, backups, tested restore procedures, timeouts, and idempotent retries. |
| Delivery | Build immutable artifacts, scan dependencies/images, deploy through CI/CD, run migrations safely, and roll back with a verified plan. |

## 4. Architecture prompt: design a production order API

Use this as a practice prompt:

> Design a Spring Boot order API that must handle traffic spikes, persist orders reliably, notify downstream services, and run on AWS.

One reasonable starting point is:

```text
Client
  |
  +--> API Gateway or ALB
          |
          +--> Spring Boot service on ECS/Fargate across multiple AZs
                    |
                    +--> RDS/Aurora for transactional order data
                    |
                    +--> Outbox record in the same database transaction
                              |
                              +--> Publisher --> SQS / EventBridge / Kafka
                                                |
                                                +--> idempotent downstream consumers

All components --> metrics, structured logs, traces --> CloudWatch / observability platform
Secrets --> Secrets Manager     Permissions --> IAM roles     Files --> S3
```

A senior explanation should add:

- An idempotency key for `POST /orders` so client retries do not create duplicate orders.
- Validation and a stable error response at the API boundary.
- A database transaction for the order and outbox record, avoiding a "database committed but event lost" failure mode.
- At-least-once message handling with consumer-side idempotency and a DLQ.
- Explicit timeouts, bounded retries, backoff, and circuit breaking for remote calls.
- Health/readiness probes, dashboards, alarms, deployment rollback, backups, and a recovery plan.
- Encryption in transit and at rest, least-privilege IAM roles, and auditability.

There are multiple valid designs. State assumptions first, then defend the simplest design that meets the requirements.

## 5. A concise system-design answer structure

When given a design question, use this order:

1. Clarify users, traffic, latency, availability, data consistency, and compliance requirements.
2. Describe the API and data model.
3. Draw the smallest working architecture.
4. Identify data ownership and synchronous versus asynchronous boundaries.
5. Address failure modes: retries, timeouts, duplicates, partial failures, and graceful degradation.
6. Address security: authentication, authorization, secrets, encryption, network boundaries, and audit logs.
7. Address observability: logs, metrics, traces, dashboards, alerts, and SLOs.
8. Address deployment and evolution: CI/CD, migrations, feature flags, backward compatibility, and rollback.
9. Name trade-offs and the next evidence you would gather before adding complexity.

## 6. Behavioral and senior-engineering preparation

Prepare concise STAR stories (Situation, Task, Action, Result) for:

- A production incident you diagnosed and prevented from recurring.
- A disagreement where you used data and trade-offs to reach a decision.
- A design you simplified instead of over-engineering.
- A time you improved reliability, performance, security, or developer experience.
- Mentoring someone through a difficult technical problem.
- Giving or receiving challenging code-review feedback.
- Explaining a technical choice to a non-technical stakeholder or client.

For each story, quantify impact where possible: latency reduced, incidents reduced, deployment time shortened, costs avoided, or team throughput improved. Be honest about what you would do differently.

## 7. Practice routine

For each study session:

1. Read one concept.
2. Implement a small version in code.
3. Write a five-sentence explanation without notes.
4. Ask yourself what fails under concurrency, load, deployment, or partial outage.
5. Add or describe the test and operational signal that would catch the failure.

This turns memorized terminology into senior-level engineering reasoning.

## 8. Recommended next milestones for this repository

1. Finish the exercises in [Spring fundamentals](01-spring-fundamentals.md).
2. Add `spring-boot-starter-web` and build a small `GameController` with DTOs and validation.
3. Add global error handling with `@RestControllerAdvice`.
4. Add PostgreSQL, Flyway migrations, Spring Data JPA, transactions, and tests.
5. Add Actuator health endpoints and structured logging.
6. Containerize the service with Docker.
7. Design an ECS/Fargate deployment with RDS, S3, IAM roles, Secrets Manager, CloudWatch, and CI/CD.
8. Add one asynchronous workflow using SQS or EventBridge and explain idempotency and DLQ handling.

## Official references

- [Spring Framework Core documentation](https://docs.spring.io/spring-framework/reference/core.html)
- [Spring Boot reference documentation](https://docs.spring.io/spring-boot/documentation.html)
- [AWS Well-Architected Framework](https://docs.aws.amazon.com/wellarchitected/latest/framework/welcome.html)
- [AWS IAM best practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [Amazon ECS on AWS Fargate](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [AWS Lambda developer guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
