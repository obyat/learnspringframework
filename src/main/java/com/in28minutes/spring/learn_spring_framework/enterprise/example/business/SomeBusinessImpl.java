package com.in28minutes.spring.learn_spring_framework.enterprise.example.business;

import com.in28minutes.spring.learn_spring_framework.enterprise.example.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;


public class SomeBusinessImpl {

    private DataService dataService;

    @Autowired
    public SomeBusinessImpl(DataService dataService) {
        this.dataService = dataService;
    }


    public int findTheGreatestFromAllData(){
        int[] data = dataService.retrieveAllData();
        int greatestValue = Integer.MIN_VALUE;
        for(int value: data) {
            if (value > greatestValue){
                greatestValue = value;
            }
        }
        return greatestValue;
    }

    interface DataService {
        int[] retrieveAllData();
    }
}
