package com.in28minutes.spring.learn_spring_framework.functionalprogramming;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Component
public class FP02Optional {
    public void optionals(){
        List<String> fruits = List.of("apple", "banana", "orange");
        Predicate<? super String> pred = fruit -> fruit.startsWith("c");

        Optional<String> fruitsWithbOptional = fruits.stream().filter(pred).findAny();
        System.out.println(fruitsWithbOptional);
        System.out.println(fruitsWithbOptional.isPresent());
        System.out.println(fruitsWithbOptional.isEmpty());
        System.out.println(fruitsWithbOptional.stream().findAny().orElse("No fruit found"));

    }
}
