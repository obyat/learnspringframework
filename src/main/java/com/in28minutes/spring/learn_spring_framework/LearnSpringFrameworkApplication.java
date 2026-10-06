package com.in28minutes.spring.learn_spring_framework;

import com.in28minutes.spring.learn_spring_framework.enterprise.example.web.MyWebController;
import com.in28minutes.spring.learn_spring_framework.enterprise.example.service.GameRunner;
import com.in28minutes.spring.learn_spring_framework.functionalprogramming.FP01Structured;
import com.in28minutes.spring.learn_spring_framework.functionalprogramming.FP02Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;


/**
 * Application entry point for this learning project.
 *
 * <p>{@link SpringBootApplication} is a convenient shortcut for three important annotations:
 * {@code @Configuration}, {@code @EnableAutoConfiguration}, and {@code @ComponentScan}.
 * Together they let Spring Boot create an {@code ApplicationContext} (the IoC container),
 * configure common infrastructure, and discover components below this package.</p>
 */
@SpringBootApplication
public class LearnSpringFrameworkApplication {

	/**
	 * Starts Spring Boot and receives the IoC container that it creates.
	 *
	 * <p>Calling {@code getBean(...)} is useful here because this is a small learning example.
	 * In production application code, prefer constructor injection so a class does not need to
	 * look up its own dependencies.</p>
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(LearnSpringFrameworkApplication.class, args);

		printIocContainer(context);
		GameRunner runner = context.getBean(GameRunner.class);
//		runner.run();
		MyWebController controller = context.getBean(MyWebController.class);
		System.out.println(controller.returnValueFromBusinessService());


	}


	/**
	 * Prints information about the Spring IoC container for learning and debugging.
	 *
	 * <p>A bean definition describes how Spring can create a bean. The names printed here include
	 * both this application's beans (such as {@code gameRunner}) and Spring Boot infrastructure
	 * beans. The exact count varies with the Spring Boot version and dependencies.</p>
	 */
	private static void printIocContainer(ConfigurableApplicationContext context) {
		System.out.printf("%n=== Spring IoC Container ===%n");




	}
}
