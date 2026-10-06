package com.in28minutes.spring.learn_spring_framework.jpahibernate.jdbc.course.repository;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jdbc.course.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CourseJdbcCommandLineRunner implements CommandLineRunner {

    private CourseJDBCRepository repository;

    @Autowired
    public CourseJdbcCommandLineRunner(CourseJDBCRepository repository) {
        this.repository = repository;
    }


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        Course course = new Course("RAy", 1, "AWS");
        Course courseK = new Course("Kevin", 10, "KOQ");
        Course course2 = new Course("RAy", 2, "jdbc");
        Course course3 = new Course("Ray", 3, "jdbc");
        Course course4 = new Course("Ruiy", 4, "jdbc");
        repository.insert(course);
        repository.insert(courseK);
        repository.insert(course2);
        repository.insert(course3);
        repository.insert(course4);

        repository.delete(course.getId());
        System.out.println(repository.findById(course2.getId()));

        System.out.println(repository.findAll());

    }
}
