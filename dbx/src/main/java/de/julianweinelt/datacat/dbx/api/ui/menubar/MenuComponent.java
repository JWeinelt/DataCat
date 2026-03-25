package de.julianweinelt.datacat.dbx.api.ui.menubar;

import javax.swing.*;

public abstract class MenuComponent<T extends JMenuItem> {

    protected MenuComponent() {}

    public abstract T create();
}