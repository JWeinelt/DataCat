package de.julianweinelt.datacat.flow.ui;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Slf4j
public class UIManager {
    private boolean checkAvailability() {
        return !GraphicsEnvironment.isHeadless();
    }

    public void start(boolean forceHeadless) {
        log.info("Checking for availability of a desktop environment...");
        if (!checkAvailability() || forceHeadless) {
            log.info("Starting flow in headless mode.");
            return;
        }
        if (checkAvailability()) {
            log.info("Desktop environment detected. Starting UI mode.");
            initGUI();
        }
    }

    private void initGUI() {

    }
}