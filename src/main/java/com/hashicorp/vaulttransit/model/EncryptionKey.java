package com.hashicorp.vaulttransit.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptionKey {

    private String plainText;

    private String cipherText;
}
