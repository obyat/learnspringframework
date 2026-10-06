package com.in28minutes.spring.learn_spring_framework.jpahibernate.jpadata.course.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;


@Entity
public class Course {
    @Id
    private long id;
    @Column(name="name")
    private String name;
    @Column(name="author")
    private String author;


    public Course(String author, long id, String name) {
        this.author = author;
        this.id = id;
        this.name = name;
    }


    public Course(){
        //empty constructor
    }

    @Override
    public String toString() {
        return "Course{" +
                "author='" + author + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;
        return id == course.id && Objects.equals(name, course.name) && Objects.equals(author, course.author);
    }


    @Override
    public int hashCode() {
        long result = id;
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(author);
        return Math.toIntExact(result);
    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public long getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
