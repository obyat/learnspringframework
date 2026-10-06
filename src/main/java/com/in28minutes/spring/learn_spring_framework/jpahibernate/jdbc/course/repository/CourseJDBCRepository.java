package com.in28minutes.spring.learn_spring_framework.jpahibernate.jdbc.course.repository;

import com.in28minutes.spring.learn_spring_framework.jpahibernate.jdbc.course.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CourseJDBCRepository {
    @Autowired
    private JdbcTemplate springJdbcTemplate;

    public CourseJDBCRepository(JdbcTemplate springJdbcTemplate){
        this.springJdbcTemplate = springJdbcTemplate;
    }


    public void insert(Course course){
        String INSERT_QUERY = """
                insert into course (id, name, author) values(?, ?, ?);
                """;
        springJdbcTemplate.update(INSERT_QUERY, course.getId(), course.getName(), course.getAuthor());
    }

    public void delete(int id){
        String DELETE_QUERY = """
                delete FROM course where id = ?;
                """;
        springJdbcTemplate.update(DELETE_QUERY, id);
    }

    public Course findById(long id){
        String SELECT_QUERY = """
                SELECT * FROM course where id = ?;
                """;
       return springJdbcTemplate.queryForObject(SELECT_QUERY, new BeanPropertyRowMapper<>(Course.class), id);
    }

    public List<Course> findAll() {
        String SELECT_QUERY = """
            SELECT * FROM course
            """;

        return springJdbcTemplate.query(
                SELECT_QUERY,
                new BeanPropertyRowMapper<>(Course.class)
        );
    }



}
