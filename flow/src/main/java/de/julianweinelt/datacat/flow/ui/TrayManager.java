package de.julianweinelt.datacat.flow.ui;

import de.julianweinelt.datacat.flow.Flow;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
public class TrayManager {
    private TrayIcon trayIcon = null;
    private SystemTray tray;

    private static TrayManager instance;

    public TrayManager() {
        instance = this;
    }

    public static TrayManager instance() {
        return instance;
    }

    protected void init() {
        if (!SystemTray.isSupported()) {
            log.warn("System tray features are not supported by your OS.");
            return;
        }

        tray = SystemTray.getSystemTray();
    }

    protected void displayImage() {
        try {
            if (trayIcon != null) {
                if (Arrays.stream(tray.getTrayIcons()).toList().contains(trayIcon)) {
                    tray.remove(trayIcon);
                } else tray.add(trayIcon);
                return;
            }

            InputStream stream = Flow.class
                    .getResourceAsStream("/icon.png");

            if (stream == null) {
                throw new RuntimeException("Icon not found in classpath");
            }

            Image image = ImageIO.read(stream);

            trayIcon = new TrayIcon(image);

            trayIcon.addActionListener(e -> {

            });

            tray.add(trayIcon);
        } catch (AWTException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
