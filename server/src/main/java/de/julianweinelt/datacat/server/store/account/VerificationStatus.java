package de.julianweinelt.datacat.server.store.account;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record VerificationStatus(UUID id, String name, Color color) {
    private static final List<VerificationStatus> registered = new ArrayList<>();

    public void register() {
        registered.add(this);
    }

    public static VerificationStatus get(UUID id) {
        return registered.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }
    public static List<VerificationStatus> getAll() {
        return registered;
    }
    public static VerificationStatus get(String name) {
        return registered.stream().filter(s -> s.name.equals(name)).findFirst().orElse(null);
    }
}
