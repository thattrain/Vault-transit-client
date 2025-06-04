package com.hashicorp.vaulttransit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyResponseDto {

    private String id;
    private String plainTextKey;
}
