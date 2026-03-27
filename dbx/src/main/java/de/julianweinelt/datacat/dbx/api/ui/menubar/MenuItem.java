package de.julianweinelt.datacat.dbx.api.ui.menubar;

import de.julianweinelt.datacat.dbx.api.Registry;
import de.julianweinelt.datacat.dbx.api.events.Event;
import de.julianweinelt.datacat.dbx.api.ui.ShortcutAction;
import de.julianweinelt.datacat.dbx.api.ui.ShortcutManager;
import lombok.Getter;

import javax.swing.*;

public final class MenuItem extends MenuComponent<JMenuItem> {
    private final String text;
    @Getter
    private final String id;
    private final JMenuItem item;

    public MenuItem(String text, String id) {
        this.text = text;
        this.id = id;
        item = new JMenuItem(text);
        item.addActionListener(e ->
                Registry.instance().callEvent(new Event("UIMenuBarItemClickEvent").set("id", id)));
    }

    public MenuItem action(Runnable action) {
        item.addActionListener(e -> {
            action.run();
        });
        return this;
    }
    public MenuItem shortcut(String name) {
        ShortcutAction action = ShortcutManager.instance().getAction(name);
        if (action == null) throw new IllegalArgumentException("No such shortcut action: " + name);
        item.setAccelerator(action.defaultKey()); //TODO: Adapt with actual config
        return this;
    }

    @Override
    protected JMenuItem create() {
        return item;
    }
}
