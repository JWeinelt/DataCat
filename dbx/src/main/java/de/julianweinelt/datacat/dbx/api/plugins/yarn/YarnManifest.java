package de.julianweinelt.datacat.dbx.api.plugins.yarn;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class YarnManifest {
    private String name;
    private final List<String> authors = new ArrayList<>();
    private String version;
    private String fileVersion;

    public YarnManifest(String name, String version, String fileVersion) {
        this.name = name;
        this.version = version;
        this.fileVersion = fileVersion;
    }
}