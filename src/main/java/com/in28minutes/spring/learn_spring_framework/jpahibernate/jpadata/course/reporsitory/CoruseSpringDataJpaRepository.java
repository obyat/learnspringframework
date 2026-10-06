package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.reporsitory;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.AuthorCourseCount;
import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CoruseSpringDataJpaRepository extends JpaRepository<Course, Long> {
    // add custom methods for custom queries must follow convention
    List<Course> findByAuthor(String author);


    @Query("""
    SELECT new com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.AuthorCourseCount(
        c.author, COUNT(c)
    )
    FROM Course c
    GROUP BY c.author
    """)
    List<AuthorCourseCount> countCoursesByAuthor();


}
