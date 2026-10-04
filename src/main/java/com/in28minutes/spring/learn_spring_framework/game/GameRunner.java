package com.in28minutes.spring.learn_spring_framework.game;

import org.springframework.stereotype.Component;

/**
 * A Spring-managed bean that uses a {@link Game}.
 *
 * <p>This class demonstrates constructor injection. Spring sees the single constructor,
 * finds the only registered {@code Game} implementation ({@link MarioGame}), and supplies it
 * when it creates this bean. {@code @Autowired} is optional on its single constructor.</p>
 */
@Component
public class GameRunner {

	/**
	 * {@code final} makes this required dependency explicit and prevents it from changing after
	 * construction.
	 */
    private final Game game;

	/**
	 * Receives a dependency from Spring rather than constructing one with {@code new}.
	 */
    public GameRunner(Game game) {
        this.game = game;
    }

	/**
	 * Uses the injected dependency through its interface.
	 */
    public void run() {
        game.up();
        game.down();
        game.left();
        game.right();
    }
}
