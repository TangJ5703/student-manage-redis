package com.biz.model;

import lombok.Data;

@Data
public class Student {
    private String id;

    private String name;

    private String birthday;

    private String description;

    private int avgScore;
}