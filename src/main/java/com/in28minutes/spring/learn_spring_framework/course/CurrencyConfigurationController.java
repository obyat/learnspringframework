package com.in28minutes.spring.learn_spring_framework.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CurrencyConfigurationController {

    public CurrencyServiceConfiguration currencyServiceConfiguration;

    @Autowired
    private CurrencyConfigurationController(CurrencyServiceConfiguration currencyServiceConfiguration) {
        this.currencyServiceConfiguration = currencyServiceConfiguration;
    }

    @RequestMapping("/currency-config")
    public CurrencyServiceConfiguration retrieveConfigs(){
        return this.currencyServiceConfiguration;
    }
}
