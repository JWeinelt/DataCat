package de.julianweinelt.datacat.server.model;

import de.julianweinelt.datacat.server.store.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MAccount {
    private final UUID uniqueID;
    private final String username;
    private final String displayName;

    public MAccount(Account account) {
        uniqueID = account.getUniqueId();
        username = account.getUsername();
        displayName = "";
    }
}
