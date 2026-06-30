package de.julianweinelt.datacat.server.store.account;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Account {
    private final UUID uniqueId;
    private String username;
    private String email;
    private String passwordHash;
    private long creationTime;
    private long lastLogin;
    private AccountStatus accountStatus;
    private VerificationStatus verificationStatus;

    private AccountPermissions permissions;

    public Account(UUID uniqueId, String username, String email, String passwordHash, long creationTime,
                   long lastLogin, AccountStatus accountStatus, VerificationStatus verificationStatus) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.creationTime = creationTime;
        this.lastLogin = lastLogin;
        this.accountStatus = accountStatus;
        this.verificationStatus = verificationStatus;
    }

    public void register() {
        AccountManager.instance().addAccount(this);
    }
}