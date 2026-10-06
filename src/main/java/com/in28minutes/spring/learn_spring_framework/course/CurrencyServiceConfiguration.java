package com.in28minutes.spring.learn_spring_framework.course;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/*
* currency-service.url=http//default.in28minutes.com
currency-service.username=defaultusername
currency-service.key=defaultkey
* **/
@Component
@ConfigurationProperties(prefix="currency-service")
public class CurrencyServiceConfiguration {

    private String url;
    private String username;
    private String key;


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
