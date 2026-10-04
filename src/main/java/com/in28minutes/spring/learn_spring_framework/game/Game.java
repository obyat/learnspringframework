package com.in28minutes.spring.learn_spring_framework.game;

/**
 * A contract for a game that can move in four directions.
 *
 * <p>{@link GameRunner} depends on this abstraction instead of a concrete game such as
 * {@link MarioGame}. This reduces coupling: Spring can inject any {@code Game} implementation
 * that has been registered as a bean. If more than one implementation is registered, make the
 * injection point unambiguous with {@code @Primary} or {@code @Qualifier}.</p>
 */
public interface Game {
    void up();

    void down();

    void left();

    void right();
}
