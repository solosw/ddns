package com.example.ddns.model.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * @author sssd
 * @created 2023-05-02-11:01
 */
@Data

public class JobTask implements Serializable {


    private Integer id;

    private String name;


    private String groupName;


    private String cronExpression;


    private String className;

    private String description;

    private Integer status;


    private transient Object executeParams;
}

