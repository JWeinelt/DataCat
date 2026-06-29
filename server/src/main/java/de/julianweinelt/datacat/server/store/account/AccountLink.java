package de.julianweinelt.datacat.server.store.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.UUID;

public record AccountLink(UUID account, AccountLinkType type, String value) {

    public JsonObject toJson() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }
}