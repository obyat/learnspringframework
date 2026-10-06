package com.in28minutes.spring.learn_spring_framework.functionalprogramming;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class FP01Structured {
    public void printAllNumbersInListStructured(List<Integer> nums){
        for (int n: nums) {
            System.out.println(n);
        }
    }

    public void printAllNumbersInListFunctional(List<Integer> nums){
       nums.stream().forEach(FP01Structured::print); // Class :: method from class
    }

    public void printEvenNums(List<Integer> nums){
        nums.stream().filter(num -> (num % 2 == 0)).forEach(System.out::println); // Class :: method from class
    }

    public static void print(int num) {
        System.out.println(num);
    }

    public void exercises(){
        // print only odd nums
        Arrays.stream(new int[] {1,2,3,4,5}).filter(a -> a % 2 == 0).forEach(System.out::println);
        String[] courses = new String[] {"spring", "Spring Boot", "API", "Microservices", "AWS", "PCF", "Docker", "Kubernetes", "DSA", "OOP"};

        //print courses on new line
        Arrays.stream(courses).forEach(System.out::println);

        //Collect courses and print them that have Spring
        Set<String> springSet = Arrays.stream(courses).map(String::toLowerCase).filter(a -> a.contains("spring")).collect(Collectors.toSet());
        springSet.forEach(System.out::println);

        // print courses whos name has 4 letters
        springSet.stream().filter(len -> len.length() >= 4).forEach(str -> System.out.printf("\ncourse: (%s: %d)", str, str.length()));


    }
}
