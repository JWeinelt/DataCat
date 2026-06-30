package de.julianweinelt.datacat.dbx.api.ui;

import de.julianweinelt.datacat.dbx.api.ui.menubar.MenuManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UIService {
    @Getter
    private final List<SettingsPanel> settingsPanels = new ArrayList<>();

    @Getter
    private final MenuManager menuManager;
    @Getter
    private final ShortcutManager shortcutManager;

    private static UIService instance = null;
    public static UIService instance() {
        return instance;
    }

    public UIService() {
        if (instance != null) throw new IllegalStateException("The UIService has already been initialized.");
        instance = this;
        log.info("UIService instance created");
        shortcutManager = new ShortcutManager();
        menuManager = new MenuManager();
    }

    public void openDialog(String clazz, Object... args) {
        try {
            Class<? extends JDialog> dialogClass =
                    Class.forName(clazz).asSubclass(JDialog.class);

            Constructor<?> constructor = findConstructor(dialogClass, args);

            JDialog dialog = (JDialog) constructor.newInstance(args);
            dialog.setVisible(true);
        } catch (Exception e) {
            log.error("Could not open dialog {}", clazz, e);
        }
    }

    public CompletableFuture<Integer> openDialogResult(String clazz, Object... args) {
        try {
            Class<? extends JDialog> dialogClass =
                    Class.forName(clazz).asSubclass(JDialog.class);

            if (!dialogClass.isAssignableFrom(ResponseDialog.class)) {
                throw new IllegalArgumentException("Class " + clazz + " is not a ResponseDialog");
            }

            Constructor<?> constructor = findConstructor(dialogClass, args);

            JDialog dialog = (JDialog) constructor.newInstance(args);
            ResponseDialog responseDialog = (ResponseDialog) dialog;
            dialog.setVisible(true);
            return responseDialog.answer();
        } catch (Exception e) {
            log.error("Could not open dialog {}", clazz, e);
        }
        return null;
    }

    private Constructor<?> findConstructor(Class<?> clazz, Object[] args)
            throws NoSuchMethodException {

        log.debug("Searching constructor for {}", clazz.getName());

        for (Constructor<?> constructor : clazz.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            log.debug("Checking constructor: {}", constructor);

            if (parameterTypes.length != args.length) {
                log.debug("Parameter count mismatch. Expected {}, got {}",
                        parameterTypes.length, args.length);
                continue;
            }

            boolean matches = true;

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> expected = parameterTypes[i];
                Class<?> actual = args[i].getClass();

                log.debug("Parameter {}: expected={}, actual={}, matches={}",
                        i,
                        expected.getName(),
                        actual.getName(),
                        expected.isAssignableFrom(actual));

                if (args[i] != null && !wrap(parameterTypes[i]).isAssignableFrom(args[i].getClass())) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                log.debug("Found matching constructor: {}", constructor);
                return constructor;
            }
        }

        throw new NoSuchMethodException("No matching constructor found");
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }

        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == char.class) return Character.class;

        return type;
    }

    /**
     * Adds a new {@link SettingsPanel} to the registry to add a new site in the settings screen.
     * @param settingsPanel The object containing all screen information
     */
    public void addSettingsPanel(SettingsPanel settingsPanel) {
        settingsPanels.add(settingsPanel);
    }
}