package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpa.course.repository;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpa.course.model.Course;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;


//@Repository
//@Transactional
public class CourseJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public void insert(Course course){
        entityManager.merge(course);
    }

    public Course findById(Long id){
       return entityManager.find(Course.class, id);
    }

    public void deleteById(long id){
        Course course = entityManager.find(Course.class, id);
        entityManager.remove(course);
    }
}
