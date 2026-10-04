package com.in28minutes.spring.learn_spring_framework.game;

import org.springframework.stereotype.Component;


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
