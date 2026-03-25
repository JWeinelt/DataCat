package de.julianweinelt.datacat.dbx.api.ui;

import javax.swing.*;

public interface ShortcutAction {
    KeyStroke defaultKey();
    String internalName();
    String displayName();
}