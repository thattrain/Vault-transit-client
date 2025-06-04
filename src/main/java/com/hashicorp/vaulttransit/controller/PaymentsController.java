package com.hashicorp.vaulttransit.controller;


import com.hashicorp.vaulttransit.VaultTransit;
import com.hashicorp.vaulttransit.VaultTransitProperties;
import com.hashicorp.vaulttransit.model.Payment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Controller
@ResponseBody
public class PaymentsController {
    private final JdbcClient db;
    private final VaultTransit vaultTransit;

    public PaymentsController(DataSource dataSource,
                       VaultTransitProperties vaultTransitProperties,
                       VaultTemplate vaultTemplate) {
        this.db = JdbcClient.create(dataSource);
        this.vaultTransit = new VaultTransit(vaultTransitProperties, vaultTemplate);
    }

    @GetMapping("/payments")
    public Collection<Payment> getPayments() {
        return this.db
                .sql("SELECT * FROM payments")
                .query((rs, rowNum) -> new Payment(
                        rs.getString("id"),
                        rs.getString("name"),
                        vaultTransit.decrypt(rs.getString("cc_info")),
                        rs.getTimestamp("created_at").toInstant()
                ))
                .list();
    }

    @PostMapping(path = "/payments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Payment> createPayment(@RequestBody Payment request) {
        var id = UUID.randomUUID().toString();
        var statement = String.format(
                "INSERT INTO payments(id, name, cc_info, created_at) "
                        + "VALUES('%s', '%s', '%s', '%s')",
                id,
                request.getName(),
                vaultTransit.encrypt(request.getCcInfo()),
                Instant.now().toString());
        this.db.sql(statement).update();
        return this.db
                .sql(String.format("SELECT * FROM payments WHERE id = '%s'", id))
                .query((rs, rowNum) -> new Payment(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("cc_info"),
                        rs.getTimestamp("created_at").toInstant()
                )).list();
    }

}
