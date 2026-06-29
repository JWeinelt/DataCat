package de.julianweinelt.datacat.server.store.account;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AccountLinkType(UUID id, String displayName, String iconName, String regex) {
    private static final List<AccountLinkType> registered = new ArrayList<>();

    public void register() {
        registered.add(this);
    }

    public static List<AccountLinkType> getAll() {
        return registered;
    }

    public static AccountLinkType get(String name) {
        return registered.stream().filter(s -> s.displayName.equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public static AccountLinkType get(UUID id) {
        return registered.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }

    @NotNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof AccountLinkType other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
