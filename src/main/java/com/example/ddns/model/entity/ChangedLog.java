package com.example.ddns.model.entity;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author sssd
 * @careate 2023-10-31-16:43
 */
@Data

public class ChangedLog implements Serializable {

    private Long id;

    private String content;


    private LocalDateTime insertDate;
}
