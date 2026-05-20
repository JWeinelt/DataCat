package de.julianweinelt.datacat.dbx.api.drivers;

import de.julianweinelt.datacat.dbx.api.exceptions.NoDriverFoundException;

import java.util.ArrayList;
import java.util.List;

public class DriverDownloadManager {
    private final List<PluginDriver> registeredDrivers = new ArrayList<>();

    public void register(PluginDriver driver) {
        registeredDrivers.add(driver);
    }

    public DriverDownloadWrapper.DriverDownload fromDB(String db, String version) {
        PluginDriver driver = byName(db);
        if (driver == null) throw new NoDriverFoundException(db);
        return new DriverDownloadWrapper.DriverDownload(driver.downloadURL(version), driver.isZippedFile(),
                generateFileName(driver, version));
    }

    private String generateFileName(PluginDriver driver, String version) {
        return driver.getInternalName() + "-" + version + "." + (driver.isZippedFile() ? driver.archiveType() : "jar");
    }

    private PluginDriver byName(String name) {
        for (PluginDriver d : registeredDrivers) if (d.getInternalName().equals(name)) return d;
        return null;
    }
}
