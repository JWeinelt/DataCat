package de.julianweinelt.datacat.server.store;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Author {
    private final UUID uniqueId;
    private String username;
    private String email;
    private byte[] passwordHash;

    public Author(UUID uniqueId, String username, String email, byte[] passwordHash) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

}