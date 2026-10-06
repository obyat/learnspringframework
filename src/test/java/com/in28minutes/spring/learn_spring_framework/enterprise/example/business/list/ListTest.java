package com.in28minutes.spring.learn_spring_framework.enterprise.example.business.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

// Connects Mockito to JUnit 5 and initializes fields marked with @Mock before each test.
@ExtendWith(MockitoExtension.class)
public class ListTest {
    // A mock has no real List state or behavior unless we explicitly stub it.
    // In production code, prefer a parameterized type such as List<String>.
    @Mock
    List list;



    @Test
    void mocksList() {
    // Stubbing: every call to size() will return 3; no real list is involved.
    when(list.size()).thenReturn(3);

    // A single stubbed value is reused for all subsequent matching invocations.
    assertEquals(list.size(), 3);
    assertEquals(list.size(), 3);
    assertEquals(list.size(), 3);
    assertEquals(list.size(), 3);
    assertEquals(list.size(), 3);
    assertEquals(list.size(), 3);
    }

    @Test
    void name() {
        // Consecutive stubbing: calls return 3, then 2, then 5.
        // Once the configured values are exhausted, Mockito keeps returning the last one (5).
        when(list.size()).thenReturn(3).thenReturn(2).thenReturn(5);

        assertEquals(list.size(), 3);
        assertEquals(list.size(), 2);
        assertEquals(list.size(), 5);
        assertEquals(list.size(), 5);
        assertEquals(list.size(), 5);
    }


    @Test
    void params() {
        // Argument matcher: anyInt() matches every primitive int argument.
        // Matchers are used only when stubbing or verifying mock interactions.
        when(list.get(anyInt())).thenReturn("test");

        // This does not perform a real List bounds check; it is only mocked behavior.
        assertEquals("test", list.get(0));
        assertEquals("test", list.get(1));
    }


}
