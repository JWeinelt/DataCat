package de.julianweinelt.datacat.dbx.api.ui;

import de.julianweinelt.datacat.dbx.api.ui.menubar.MenuManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Adds a new {@link SettingsPanel} to the registry to add a new site in the settings screen.
     * @param settingsPanel The object containing all screen information
     */
    public void addSettingsPanel(SettingsPanel settingsPanel) {
        settingsPanels.add(settingsPanel);
    }
}