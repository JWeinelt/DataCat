package de.julianweinelt.datacat.server.store;

import java.util.UUID;

public record Yarn(UUID uniqueId, String name, UUID authorId) {}