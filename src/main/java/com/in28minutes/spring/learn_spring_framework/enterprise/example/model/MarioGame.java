package com.in28minutes.spring.learn_spring_framework.enterprise.example.model;

import org.springframework.stereotype.Component;

/**
 * One concrete {@link Game} implementation.
 *
 * <p>{@code @Component} makes this class discoverable during component scanning. Spring creates
 * one singleton bean by default, named {@code marioGame}, and can inject it wherever a
 * {@code Game} is required.</p>
 */
@Component
public class MarioGame implements Game {

    public void up(){
        System.out.println("mario up");
    }

    public void down(){
        System.out.println("mario down");
    }

    public void left(){
        System.out.println("mario left");
    }

    public void right(){
        System.out.println("mario right");
    }
}
