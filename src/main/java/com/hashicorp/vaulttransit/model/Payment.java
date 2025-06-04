package com.hashicorp.vaulttransit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Payment {

    private String id;

    private String name;

    @JsonProperty("cc_info")
    private String ccInfo;

    private Instant createdAt;
}
