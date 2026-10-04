package com.in28minutes.spring.learn_spring_framework;

import com.in28minutes.spring.learn_spring_framework.game.GameRunner;
import com.in28minutes.spring.learn_spring_framework.game.MarioGame;
import com.in28minutes.spring.learn_spring_framework.game.PacMan;
import com.in28minutes.spring.learn_spring_framework.game.SuperContraGame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class LearnSpringFrameworkApplication {

	static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(LearnSpringFrameworkApplication.class, args);


//		MarioGame game = new MarioGame();
//		PacMan game = new PacMan();
//		SuperContraGame game = new SuperContraGame(); // first
//		GameRunner runner = new GameRunner(game);  // second

		GameRunner runnerBean = context.getBean(GameRunner.class);
		runnerBean.run();

	}
}