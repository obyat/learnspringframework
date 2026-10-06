package com.in28minutes.spring.learn_spring_framework.functionalprogramming;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A runnable interview-study guide that combines the ideas from FP01Structured and FP02Optional.
 *
 * <p>Run {@link #main(String[])} directly from your IDE. This class deliberately has no Spring
 * dependencies: the examples are plain Java and are easier to study in isolation.</p>
 */
public class FunctionalProgrammingStudyGuide {

    private static final List<Integer> NUMBERS = List.of(1, 2, 3, 4, 5);
    private static final List<String> COURSES = List.of(
            "Spring", "Spring Boot", "API", "Microservices", "AWS",
            "PCF", "Docker", "Kubernetes", "DSA", "OOP");
    private static final List<String> FRUITS = List.of("apple", "banana", "orange");

    /**
     * Entry point for running every example in a predictable order.
     */
    public static void main(String[] args) {
        new FunctionalProgrammingStudyGuide().runStudyExamples();
    }

    /**
     * Keeps the study flow intentional: imperative code first, streams second, Optional last.
     */
    public void runStudyExamples() {
        printSection("1. Structured (imperative) iteration");
        printAllNumbersStructured(NUMBERS);

        printSection("2. Functional iteration with a stream");
        printAllNumbersFunctional(NUMBERS);

        printSection("3. Filter even numbers");
        printEvenNumbers(NUMBERS);

        printSection("4. Stream pipeline: filter, map, and collect");
        printAllCourses(COURSES);
        printCoursesContainingSpring(COURSES);
        printCoursesWithAtLeastFourCharacters(COURSES);

        printSection("5. Optional: a value may be present or absent");
        demonstrateOptional(FRUITS, "b");
        demonstrateOptional(FRUITS, "c");


        printSection("5. Functional Interface: passing anonymous classes interfaces as method references");
        testFunctionalInterface();
    }

    private void printAllNumbersStructured(List<Integer> numbers) {
        // Imperative/structured style: we describe HOW to loop through the list.
        for (int number : numbers) {
            System.out.println(number);
        }
    }

    private void printAllNumbersFunctional(List<Integer> numbers) {
        // Stream pipeline: source -> terminal operation. forEach starts the traversal.
        // System.out::println is a method reference; it is shorthand for number -> System.out.println(number).
        numbers.stream().forEach(System.out::println);
    }

    private void printEvenNumbers(List<Integer> numbers) {
        // filter is an intermediate, lazy operation; forEach is the terminal operation that runs the pipeline.
        numbers.stream()
                .filter(number -> number % 2 == 0)
                .forEach(System.out::println);
    }

    private void printCoursesContainingSpring(List<String> courses) {
        List<String> springCourses = courses.stream()
                // map transforms each element without changing the original list.
                .map(String::toLowerCase)
                .filter(course -> course.contains("spring"))
                // toList is a terminal operation; this result keeps the encounter order.
                .toList();

        System.out.println("Courses containing 'spring': " + springCourses);
    }

    private void printAllCourses(List<String> courses) {
        // This is the stream equivalent of printing every course with an enhanced for-loop.
        System.out.println("All courses:");
        courses.stream().forEach(System.out::println);
    }

    private void printCoursesWithAtLeastFourCharacters(List<String> courses) {
        System.out.println("Courses with at least 4 characters:");
        courses.stream()
                .filter(course -> course.length() >= 4)
                .forEach(course -> System.out.printf("%s (%d characters)%n", course, course.length()));
    }

    private void demonstrateOptional(List<String> fruits, String prefix) {
        Optional<String> matchingFruit = findFruitStartingWith(fruits, prefix);

        // Optional avoids returning null when a search may not find a value.
        System.out.printf("First fruit starting with '%s': %s%n",
                prefix, matchingFruit.orElse("No fruit found"));

        // Optional.stream() is useful inside a larger Stream pipeline; it is unnecessary for this simple fallback.
        // Prefer safe Optional operations such as orElse and ifPresent over calling get().
        matchingFruit.ifPresent(fruit -> System.out.println("Found value: " + fruit));
        System.out.println("Value present? " + matchingFruit.isPresent());
    }

    private Optional<String> findFruitStartingWith(List<String> fruits, String prefix) {
        // Predicate<T> represents a function that takes T and returns boolean; filter uses it to keep matches.
        Predicate<String> startsWithPrefix = fruit -> fruit.startsWith(prefix);

        // findFirst returns Optional because the stream might contain no matching element.
        // Interview note: findFirst preserves encounter order; findAny may be preferable in parallel pipelines.
        return fruits.stream()
                .filter(startsWithPrefix)
                .findFirst();
    }

    private void printSection(String title) {
        System.out.printf("%n=== %s ===%n", title);
    }

    private void testFunctionalInterface(){
        Calculator add      = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;

        System.out.println(add.calculate(3, 4));        // 7
        System.out.println(multiply.calculate(3, 4));        // 12
    }
}
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}