package de.julianweinelt.datacat.ui.importdiag;

public enum ImportMode {
    RECREATE(false),
    RECREATE_DUPLICATES(false),
    APPEND(true);

    public final boolean isBeta;

    ImportMode(boolean isBeta) {
        this.isBeta = isBeta;
    }
}
