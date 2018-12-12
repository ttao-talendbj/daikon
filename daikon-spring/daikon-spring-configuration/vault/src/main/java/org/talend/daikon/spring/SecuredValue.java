package org.talend.daikon.spring;

import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Ciphertext;

public class SecuredValue {

    private final VaultOperations vaultOperations;

    private final String name;

    private final String keyName;

    public SecuredValue(VaultOperations vaultOperations, String name) {
        this.vaultOperations = vaultOperations;
        this.name = name;
        this.keyName = "";
    }

    public String value() {
        final VaultTransitOperations transitOperations = vaultOperations.opsForTransit();
        return transitOperations.decrypt(keyName, Ciphertext.of(name)).asString();
    }

    @Override
    public String toString() {
        return "SecuredValue{}";
    }
}
