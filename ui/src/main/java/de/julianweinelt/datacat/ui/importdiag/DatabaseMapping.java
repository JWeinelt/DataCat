package de.julianweinelt.datacat.ui.importdiag;

import lombok.Getter;

import javax.swing.*;
import java.util.Objects;

public class DatabaseMapping {

    @Getter
    private final String sourceDatabase;

    private JComboBox<DatabaseMap> targetCombo;

    public DatabaseMapping(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public String getTargetDatabase(String textField) {
        DatabaseMap value = (DatabaseMap) targetCombo.getSelectedItem();

        if (value == null) return null;
        if (value.mode().equals(DatabaseMapMode.IGNORE)) return null;
        if (value.mode().equals(DatabaseMapMode.CREATE_NEW)) return textField;
        if (value.mode().equals(DatabaseMapMode.MAP)) return value.targetDatabase();

        return null;
    }

    public boolean createNew() {
        return ((DatabaseMap) Objects.requireNonNull(targetCombo.getSelectedItem())).mode().equals(DatabaseMapMode.CREATE_NEW);
    }

    public boolean ignore() {
        return ((DatabaseMap) Objects.requireNonNull(targetCombo.getSelectedItem())).mode().equals(DatabaseMapMode.IGNORE);
    }
}