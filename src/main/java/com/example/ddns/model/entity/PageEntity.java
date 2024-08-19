package com.example.ddns.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sssd
 * @created 2023-04-18-15:13
 */
@Data
public class PageEntity implements Serializable {

    @JsonProperty("page")
    private Long page ;

    @JsonProperty("pageSize")
    private Long pageSize ;
}
