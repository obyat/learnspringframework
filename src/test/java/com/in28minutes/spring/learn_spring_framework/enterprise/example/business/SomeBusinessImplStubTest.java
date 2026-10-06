package com.in28minutes.spring.learn_spring_framework.enterprise.example.business;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SomeBusinessImplStubTest {

    @Test
    void test() {
        DataServiceStub dataServiceStub = new DataServiceStub();
        SomeBusinessImpl someBusiness = new SomeBusinessImpl(dataServiceStub);
        int val = someBusiness.findTheGreatestFromAllData();
        assertEquals(25, val);
    }
}

class DataServiceStub implements SomeBusinessImpl.DataService {
    @Override
    public int[] retrieveAllData() {
        return new int[] {25, 15, 5};
    }
}