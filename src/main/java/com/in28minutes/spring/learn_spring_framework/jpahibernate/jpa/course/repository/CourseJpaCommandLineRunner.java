package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpa.course.repository;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpa.course.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


//@Component
public class CourseJpaCommandLineRunner implements CommandLineRunner {

    private CourseJpaRepository repository;

    @Autowired
    public CourseJpaCommandLineRunner(CourseJpaRepository repository) {
        this.repository = repository;
    }


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
//    @Override
    public void run(String... args) throws Exception {

        Course course = new Course("RAy", 1, "AWS");
        Course course2 = new Course("RAy", 2, "jpa");
        Course course3 = new Course("Ray", 3, "jpa");
        Course course4 = new Course("Ruiy", 4, "jpa");
        repository.insert(course);
        repository.insert(course2);
        repository.insert(course3);
        repository.insert(course4);

        repository.deleteById(course.getId());
        System.out.println(repository.findById(course2.getId()));

//        System.out.println(repository.findAll());

    }
}
