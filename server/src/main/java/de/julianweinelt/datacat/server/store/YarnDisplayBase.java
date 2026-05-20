package de.julianweinelt.datacat.server.store;

import java.util.UUID;

public record YarnDisplayBase(UUID uniqueId, String name, Author author, String shortDescription, int downloads) {}