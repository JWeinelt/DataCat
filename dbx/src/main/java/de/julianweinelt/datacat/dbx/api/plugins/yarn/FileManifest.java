package de.julianweinelt.datacat.dbx.api.plugins.yarn;

public record FileManifest(String formatVersion, String checksum, String compression) {}