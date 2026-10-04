package com.in28minutes.spring.learn_spring_framework.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GameRunner {

    @Autowired
    private final Game game;

    public GameRunner(Game game){
        this.game = game;
    }

    public void run(){
        this.game.up();
        this.game.down();
        this.game.left();
        this.game.right();
    }
}
