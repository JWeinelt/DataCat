package de.julianweinelt.datacat.dbx.api.ui.menubar;

import javax.swing.*;

public abstract class MenuComponent<T extends JMenuItem> {

    protected MenuComponent() {}

    /**
     * Constructs a menu component
     * @return The {@link MenuComponent} extending a {@link JMenuItem} object
     */
    protected abstract T create();
}