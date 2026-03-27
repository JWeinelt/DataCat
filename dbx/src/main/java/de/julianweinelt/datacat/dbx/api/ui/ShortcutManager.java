package de.julianweinelt.datacat.dbx.api.ui;

import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ShortcutManager {
    @Getter
    private final List<ShortcutAction> actions = new ArrayList<>();

    public static ShortcutManager instance() {
        return UIService.instance().getShortcutManager();
    }


    public void register(ShortcutAction action) {
        actions.add(action);
    }

    /**
     * Registers a new shortcut action for use in the menu and the editor.
     * @param name A {@link String} only containing alphanumerical upper case letters and underscores for identifying the action.
     * @param displayName A display name for better readability
     * @param stroke A {@link KeyStroke} object for the default keyboard shortcut of this action
     * @throws IllegalArgumentException if the given name contains any illegal characters.
     * @apiNote Example
     * <pre>{@code
     * ShortcutManager m = ShortcutManager.instance();
     * m.register("OPEN_FILE", "Open File", KeyStroke.getKeyStroke("control O"));
     * }</pre>
     */
    public void register(String name, String displayName, KeyStroke stroke) {
        if (!name.matches("[A-Z_]")) throw new IllegalArgumentException("Name should only contain upper case alphanumerical letters and underscores.");
        actions.add(new ShortcutAction() {
            @Override
            public KeyStroke defaultKey() {
                return stroke;
            }

            @Override
            public String internalName() {
                return name;
            }

            @Override
            public String displayName() {
                return displayName;
            }
        });
    }

    /**
     * Return a registered action by its internal name.
     * @param name The internal name of the action
     * @return An {@link ShortcutAction} object containing the action, or <code>null</code> if it doesn't exist
     * @deprecated in favor of {@link #getActionOptional(String)} with null-safety
     */
    @Deprecated(forRemoval = true, since = "1.0.1")
    public ShortcutAction getAction(String name) {
        for (ShortcutAction a : actions) if (a.internalName().equals(name)) return a;
        return null;
    }

    public Optional<ShortcutAction> getActionOptional(String name) {
        for (ShortcutAction a : actions) if (a.internalName().equals(name)) return Optional.of(a);
        return Optional.empty();
    }
}
