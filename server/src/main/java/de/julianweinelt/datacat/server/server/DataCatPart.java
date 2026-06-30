package de.julianweinelt.datacat.server.server;

public enum DataCatPart {
    EDITOR("ui"),
    LAUNCHER("launcher"),
    FLOW("flow");

    public final String folder;

    DataCatPart(String folder) {
        this.folder = folder;
    }
}
