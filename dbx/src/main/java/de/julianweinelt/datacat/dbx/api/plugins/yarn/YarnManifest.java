package de.julianweinelt.datacat.dbx.api.plugins.yarn;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YarnManifest {
    private String name;
    private final List<String> authors = new ArrayList<>();
    private String version;
    private String fileVersion;
}