package com.hashicorp.vaulttransit.controller;

import com.hashicorp.vaulttransit.VaultTransit;
import com.hashicorp.vaulttransit.VaultTransitProperties;
import com.hashicorp.vaulttransit.model.EncryptionKey;
import com.hashicorp.vaulttransit.model.dto.KeyResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/datakey")
public class DataKeyController {

    private final JdbcClient db;
    private final VaultTransit vaultTransit;

    public DataKeyController(DataSource dataSource,
                             VaultTransitProperties vaultTransitProperties,
                             VaultTemplate vaultTemplate) {
        this.db = JdbcClient.create(dataSource);
        this.vaultTransit = new VaultTransit(vaultTransitProperties, vaultTemplate);
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> generateDataKey(){

        Optional<EncryptionKey> key = vaultTransit.getDataKey(128);
        if (key.isPresent()) {
            var keyId = UUID.randomUUID().toString();
            var statement = String.format(
                    "INSERT INTO event_keys(id, cipher_text) VALUES ('%s', '%s')",
                    keyId,
                    key.get().getCipherText()
            );
            this.db.sql(statement).update();
            KeyResponseDto response = new KeyResponseDto(keyId, key.get().getPlainText());
            return ResponseEntity
                    .ok(response);
        }else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Collection<KeyResponseDto> getDataKey(@RequestParam("id") String id) {
        return this.db
                .sql(String.format("SELECT * FROM event_keys WHERE id = '%s'", id))
                .query((rs, rowNum) -> new KeyResponseDto(
                        rs.getString("id"),
                        Base64.getEncoder().encodeToString(vaultTransit.decryptDataKey(rs.getString("cipher_text")))
                )).list();
    }

}
