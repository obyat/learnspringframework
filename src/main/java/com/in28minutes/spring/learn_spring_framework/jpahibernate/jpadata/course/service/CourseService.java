package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.service;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.AuthorCourseCount;
import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.Course;
import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.reporsitory.CoruseSpringDataJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CourseService {

    private final CoruseSpringDataJpaRepository repository;

    public CourseService(CoruseSpringDataJpaRepository repository) {
        this.repository = repository;
    }

    public List<AuthorCourseCount> getCourseCountsByAuthor() {
        return repository.countCoursesByAuthor().stream()
                .filter(result -> !result.author().equals("Jay"))
                .toList();
    }
}

