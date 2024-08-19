package com.example.ddns.model.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sssd
 * @careate 2023-10-26-15:40
 */
@Data
public class AmisPageEntity implements Serializable {

    @JsonProperty("page")
    private Long page ;


    @JsonProperty("perPage")
    private Long perPage ;

}
