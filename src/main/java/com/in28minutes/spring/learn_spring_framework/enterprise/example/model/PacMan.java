package com.in28minutes.spring.learn_spring_framework.enterprise.example.model;
import org.springframework.stereotype.Component;


@Component
public class PacMan implements Game {

    public void up(){
        System.out.println("pacman up");
    }

    public void down(){
        System.out.println("pacman down");
    }

    public void left(){
        System.out.println("pacman left");
    }

    public void right(){
        System.out.println("pacman right");
    }
}