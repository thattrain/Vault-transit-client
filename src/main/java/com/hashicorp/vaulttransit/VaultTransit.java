package com.hashicorp.vaulttransit;

import com.hashicorp.vaulttransit.model.EncryptionKey;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultTransitContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VaultTransit {
    private final VaultOperations vault;
    private final String path;
    private final String key;
    private final String eventsKey;
    private final String eventsDataKeyPath;

    public VaultTransit(VaultTransitProperties properties, VaultTemplate vaultTemplate) {
        this.vault = vaultTemplate;
        this.path = properties.getPath();
        this.key = properties.getKey();
        this.eventsKey = properties.getEventsKey();
        this.eventsDataKeyPath = properties.getEventsDataKeyPath();
    }

    public String decrypt(String ccInfo) {
        return vault.opsForTransit(path).decrypt(key, ccInfo);
    }

    public String encrypt(String ccInfo) {
        return vault.opsForTransit(path).encrypt(key, ccInfo);
    }

    public byte[] decryptDataKey(String cipherTextKey){
        return vault.opsForTransit(path).decrypt(eventsKey, cipherTextKey, VaultTransitContext.empty());
    }

    public Optional<EncryptionKey> getDataKey(int keyLength){
        Map<String, String> body = new HashMap<>();
        body.put("bits", String.valueOf(keyLength));
        VaultResponse response = vault.write(eventsDataKeyPath, body);
        if (response != null){
            String cipherText = (String) response.getRequiredData().get("ciphertext");
            String plaintext = (String) response.getRequiredData().get("plaintext");
            return Optional.of(new EncryptionKey(plaintext, cipherText));
        }

        return Optional.empty();
    }
}