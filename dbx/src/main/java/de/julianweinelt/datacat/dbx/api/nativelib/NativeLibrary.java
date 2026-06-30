package de.julianweinelt.datacat.dbx.api.nativelib;

import de.julianweinelt.datacat.dbx.api.TranslateString;
import org.jetbrains.annotations.Nullable;

public abstract class NativeLibrary {
    private final String name;

    protected NativeLibrary(String name) {
        this.name = name;
    }


    /**
     * Gives the download url for the library files.
     * @param platform The platform to download the library for.
     * @param version The version of the library.
     * @return The download url for the library files.
     * @implNote The parameter {@code platform} is indented to define different versions for different platforms.
     */
    public abstract @Nullable String downloadUrl(LibraryPlatform platform, String version);

    /**
     * A detailed description of what this library is for.
     * @return A string describing the library's purpose
     * @implNote This description should be in English, unless you use {@link TranslateString} to translate it.
     */
    public abstract String description();

    public String name() {
        return name;
    }
}