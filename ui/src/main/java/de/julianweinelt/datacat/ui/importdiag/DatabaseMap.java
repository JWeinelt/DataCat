package de.julianweinelt.datacat.ui.importdiag;

public record DatabaseMap(DatabaseMapMode mode, String targetDatabase) {

    public static DatabaseMap ignore() {
        return new DatabaseMap(DatabaseMapMode.IGNORE, null);
    }
    public static DatabaseMap createNew() {
        return new DatabaseMap(DatabaseMapMode.CREATE_NEW, null);
    }
}
