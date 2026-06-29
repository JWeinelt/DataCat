package de.julianweinelt.datacat.dbx.api.drivers;

import de.julianweinelt.datacat.dbx.api.VersionStatus;

public record DriverVersion(String versionName, boolean semantic, VersionStatus status) {}