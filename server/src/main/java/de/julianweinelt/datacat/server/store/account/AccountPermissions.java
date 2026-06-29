package de.julianweinelt.datacat.server.store.account;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class AccountPermissions {
    private final UUID account;
    private final List<String> permissions = new ArrayList<>();

    public AccountPermissions(UUID account) {
        this.account = account;
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
}
