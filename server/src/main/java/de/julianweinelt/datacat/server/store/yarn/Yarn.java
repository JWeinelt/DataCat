package de.julianweinelt.datacat.server.store.yarn;

import de.julianweinelt.datacat.server.model.MAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Yarn {
    private UUID uniqueID;

    private MAccount author;
    private String shortDescription;
    private String longDescription;
    private final List<String> tags = new ArrayList<>();

    private String wikiLink;
    private String discordLink;
    private String sourceLink;

    private int downloads;
}
