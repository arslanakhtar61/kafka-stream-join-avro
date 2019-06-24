package com.sample.kafkastreamjoin.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Subject {

    private String title;

    public Subject() {
    }

    public Subject(String title) {
        this.title = title;
    }

    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "title='" + title + '\'' +
                '}';
    }
}
