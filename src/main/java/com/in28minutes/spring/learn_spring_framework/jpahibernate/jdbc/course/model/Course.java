package com.in28minutes.spring.learn_spring_framework.jpahibernate.jdbc.course.model;

import java.util.Objects;


public class Course {
    private int id;
    private String name;
    private String author;


    public Course(String author, int id, String name) {
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
        int result = id;
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(author);
        return result;
    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public int getId() {
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
