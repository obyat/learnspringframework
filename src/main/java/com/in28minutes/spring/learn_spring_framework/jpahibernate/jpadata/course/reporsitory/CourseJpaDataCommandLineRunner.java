package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.reporsitory;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model.Course;
import com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CourseJpaDataCommandLineRunner implements CommandLineRunner {

    private CoruseSpringDataJpaRepository repository;
    private CourseService courseService;

    @Autowired
    public CourseJpaDataCommandLineRunner(CoruseSpringDataJpaRepository repository, CourseService courseService) {
        this.repository = repository;
        this.courseService = courseService;
    }


    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

        Course course = new Course("RAy", 11, "AWS1");
        Course course2 = new Course("RAy", 12, "Datajpa");
        Course course21 = new Course("Kevin", 121, "Datajpa");
        Course course3 = new Course("Ray", 13, "Datajpa");
        Course course4 = new Course("Ruiy", 14, "Datajpa");
        repository.save(course);
        repository.save(course2);
        repository.save(course21);
        repository.save(course3);
        repository.save(course4);

        repository.deleteById(course.getId());

        System.out.println(repository.findById(course2.getId()));
        System.out.println(repository.findAll());
        System.out.println(repository.count());
        System.out.println(repository.findByAuthor("Kevin")); // actually found from both jdbc and jpa because they share same h2 db

        System.out.println(courseService.getCourseCountsByAuthor());
    }
}
