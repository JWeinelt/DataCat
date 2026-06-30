package de.julianweinelt.datacat.dbx.api.nativelib;

public enum LibraryPlatform {
    WINDOWS(".dll"),
    LINUX(".so"),
    MACOS(".dylib"),
    ;

    public final String fileEnding;

    LibraryPlatform(String fileEnding) {
        this.fileEnding = fileEnding;
    }
}
