package de.julianweinelt.datacat.server.server;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Version {
    private final DataCatPart part;
    private final String versionID;
    private final long creationTime;
    @Setter
    private boolean supported = true;

    public Version(DataCatPart part, String versionID) {
        this.part = part;
        this.versionID = versionID;
        creationTime = System.currentTimeMillis();
    }
}