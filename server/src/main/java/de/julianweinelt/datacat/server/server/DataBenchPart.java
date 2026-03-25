package de.julianweinelt.datacat.server.server;

public enum DataBenchPart {
    EDITOR("ui"),
    LAUNCHER("launcher"),
    FLOW("flow");

    public final String folder;

    DataBenchPart(String folder) {
        this.folder = folder;
    }
}
