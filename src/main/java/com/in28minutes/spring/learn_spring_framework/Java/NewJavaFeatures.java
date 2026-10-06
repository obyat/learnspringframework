package com.in28minutes.spring.learn_spring_framework.Java;

import java.time.DayOfWeek;

/**
 * A runnable interview-study guide for modern Java language features.
 *
 * <p>Run {@link #main(String[])} directly from your IDE. Each section is independent so it can
 * be reviewed or discussed in an interview on its own.</p>
 */
public class NewJavaFeatures {

    /**
     * Standard Java entry point: accepts command-line arguments even when this example does not use them.
     */
    public static void main(String[] args) {
        runStudyExamples();
    }

    private static void runStudyExamples() {
        printSection("1. Java records");
        demonstrateRecords();

        printSection("2. Switch expressions");
        demonstrateSwitchExpression(DayOfWeek.FRIDAY);
        demonstrateSwitchExpression(DayOfWeek.WEDNESDAY);
    }

    private static void demonstrateRecords() {
        Person person = new Person("Kevin", 20);
        Person personWithSameValues = new Person("Kevin", 20);

        System.out.println("Record: " + person);
        System.out.println("Accessor methods: name=" + person.name() + ", age=" + person.age());
        System.out.println("Value-based equality: " + person.equals(personWithSameValues));
    }

    private static void demonstrateSwitchExpression(DayOfWeek day) {
        // A switch expression evaluates to a value, which can be assigned to a variable.
        String result = switch (day) {
            // Arrow labels do not fall through to later cases.
            case MONDAY, FRIDAY -> "Weekend is near!";
            case SATURDAY, SUNDAY -> "It is the weekend!";
            default -> "Regular day";
        };

        System.out.printf("%s: %s%n", day, result);
    }

    private static void printSection(String title) {
        System.out.printf("%n=== %s ===%n", title);
    }

    /**
     * A record is a concise data carrier. Java generates accessors, equals, hashCode, toString,
     * and—when no constructor is declared—the canonical constructor from its components.
     *
     * <p>Records are implicitly final, have no setters, and can implement interfaces but cannot
     * extend another class. They are shallowly immutable: component references cannot be
     * reassigned, but a mutable object stored in a component could still change.</p>
     */
    private record Person(String name, int age) {

        // A compact canonical constructor is the natural place to validate record components.
        Person {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            if (age < 0) {
                throw new IllegalArgumentException("age must not be negative");
            }
        }
    }
}
