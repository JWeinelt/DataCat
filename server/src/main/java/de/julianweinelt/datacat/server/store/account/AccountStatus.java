package de.julianweinelt.datacat.server.store.account;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AccountStatus(UUID id, String name, Color color) {
    private static final List<AccountStatus> registered = new ArrayList<>();

    public void register() {
        registered.add(this);
    }

    public static AccountStatus get(UUID id) {
        return registered.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }
    public static List<AccountStatus> getAll() {
        return registered;
    }
    public static AccountStatus get(String name) {
        return registered.stream().filter(s -> s.name.equals(name)).findFirst().orElse(null);
    }
}
