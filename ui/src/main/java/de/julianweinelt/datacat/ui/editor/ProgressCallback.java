package de.julianweinelt.datacat.ui.editor;

@FunctionalInterface
public interface ProgressCallback {
    void update(int currentRow);
}
