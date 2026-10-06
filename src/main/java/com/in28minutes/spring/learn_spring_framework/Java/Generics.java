package com.in28minutes.spring.learn_spring_framework.Java;

import java.util.ArrayList;
import java.util.List;

/**
 * A runnable interview-study guide for Java generics and wildcards.
 *
 * <p>Run {@link #main(String[])} directly from your IDE. The examples progress from a named
 * type parameter ({@code T}) to wildcard bounds ({@code ? extends} and {@code ? super}).</p>
 */
public class Generics {

    public static void main(String[] args) {
        runStudyExamples();
    }

    private static void runStudyExamples() {
        printSection("1. Generic class: type-safe container");
        demonstrateGenericClass();

        printSection("2. Generic method: one named type parameter");
        demonstrateGenericMethod();

        printSection("3. Wildcards: ?, ? extends, and ? super");
        demonstrateWildcards();
    }

    private static void demonstrateGenericClass() {
        Box<String> messageBox = new Box<>("Generics prevent unsafe casts.");
        messageBox.setValue("Generics provide compile-time type safety.");

        // The compiler knows getValue() returns String; no cast is needed.
        System.out.println(messageBox.getValue());
        // messageBox.setValue(42); // Does not compile: Box<String> only accepts Strings.
    }

    private static void demonstrateGenericMethod() {
        List<String> names = List.of("Ada", "Grace", "Linus");

        // The same T is used for the List element and the return value.
        String firstName = firstItem(names);
        System.out.println("First name: " + firstName);
    }

    private static void demonstrateWildcards() {
        List<Integer> integers = List.of(1, 2, 3, 4);
        List<Double> decimals = List.of(1.5, 2.5);

        // ? extends Number accepts a List<Integer>, List<Double>, and other Number subtypes.
        System.out.println("Integer total: " + sumNumbers(integers));
        System.out.println("Decimal total: " + sumNumbers(decimals));

        List<Number> scoreSink = new ArrayList<>(List.of(80));
        addDefaultScore(scoreSink);
        System.out.println("Scores after ? super Integer: " + scoreSink);

        printUnknownType(List.of("Spring", "Java", "Generics"));
    }

    private static <T> T firstItem(List<T> items) {
        return items.get(0);
    }

    private static double sumNumbers(List<? extends Number> numbers) {
        double total = 0;

        // Producer extends: values can safely be read as Number.
        for (Number number : numbers) {
            total += number.doubleValue();
        }

        // numbers.add(1); // Not safe: the actual list could be a List<Double>.
        return total;
    }

    private static void addDefaultScore(List<? super Integer> scores) {
        // Consumer super: Integer values can be added to List<Integer>, List<Number>, or List<Object>.
        scores.add(100);

        // Reading is only safely typed as Object because the list might be List<Object>.
        Object addedScore = scores.get(scores.size() - 1);
        System.out.println("Added score: " + addedScore);
    }

    private static void printUnknownType(List<?> values) {
        // ? means an unknown element type. It is safe to read each value as Object.
        values.forEach(value -> System.out.println("Unknown-type value: " + value));

        // values.add("Java"); // Not safe: the unknown list might be a List<Integer>.
    }

    private static void printSection(String title) {
        System.out.printf("%n=== %s ===%n", title);
    }

    /**
     * T means a caller-selected type. This class can store a String, Integer, Person, and so on.
     *
     * <p>Interview note: generic types are invariant, so {@code List<String>} is not a subtype of
     * {@code List<Object>}. Wildcards provide safe flexibility when a method needs it.</p>
     */
    private static final class Box<T> {
        private T value;

        private Box(T value) {
            this.value = value;
        }

        private T getValue() {
            return value;
        }

        private void setValue(T value) {
            this.value = value;
        }
    }

}
