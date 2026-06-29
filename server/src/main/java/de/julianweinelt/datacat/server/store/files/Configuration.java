package de.julianweinelt.datacat.server.store.files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Configuration {
    private String mailServer = "";
    private String mailPort = "";
    private String mailUser = "";
    private String mailPassword = "";
    private String mailFrom = "";

    private int tokenLifetime = 60*60*24*7; // 1 week
    private String jwtSecret = "";

    public static Configuration instance() {
        return LocalStorage.instance().getConfig();
    }
}
