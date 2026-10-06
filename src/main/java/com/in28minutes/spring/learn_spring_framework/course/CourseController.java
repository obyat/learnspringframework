package com.in28minutes.spring.learn_spring_framework.course;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
public class CourseController {


    @RequestMapping("/courses")
    public List<Course> retrieveAllCourses(){
        return Arrays.asList(
          new Course("in28mins", 1, "learn AWS"),
          new Course("in28mins", 2, "springboot"),
          new Course("in28mins", 3, "aws")
        );
    }
}
