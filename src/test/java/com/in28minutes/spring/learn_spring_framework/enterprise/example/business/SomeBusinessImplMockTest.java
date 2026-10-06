package com.in28minutes.spring.learn_spring_framework.enterprise.example.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SomeBusinessImplMockTest {

    @Mock
    private SomeBusinessImpl.DataService dataServiceMock; // SomeBusinessImpl.DataService dataServiceMock = mock(DataServiceStub.class);

    @InjectMocks
    SomeBusinessImpl someBusiness;


    @Test
    void findTheGreatestFromAllData() {
        when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {25, 15, 5});

        int result = someBusiness.findTheGreatestFromAllData();
        assertEquals(25, result);
    }


    @Test
    void findTheGreatestFromAllDataOneValue() {
        when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {35});

        int result = someBusiness.findTheGreatestFromAllData();
        assertEquals(35, result);
    }
}