package de.julianweinelt.datacat.dbx.api.drivers;

import de.julianweinelt.datacat.dbx.api.exceptions.NoDriverFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class DriverDownloadManager {
    @Getter
    private final List<PluginDriver> registeredDrivers = new ArrayList<>();

    private static DriverDownloadManager instance;

    public static DriverDownloadManager instance() {
        return instance;
    }
    public DriverDownloadManager() {
        instance = this;
    }

    public void register(PluginDriver driver) {
        log.info("Registering plugin driver for {}", driver.getInternalName());
        registeredDrivers.add(driver);
    }

    public PluginDriver[] registeredDrivers() {
        PluginDriver[] drivers = new PluginDriver[registeredDrivers.size()];
        return registeredDrivers.toArray(drivers);
    }

    public PluginDriver byInternalName(String internalName) {
        for (PluginDriver driver : registeredDrivers) if (driver.getInternalName().equals(internalName)) return driver;
        return null;
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

    public PluginDriver byName(String name) {
        for (PluginDriver d : registeredDrivers) if (d.getInternalName().equals(name)) return d;
        return null;
    }

    public List<String> registeredDriverNames() {
        return registeredDrivers.stream()
                .map(PluginDriver::getInternalName)
                .toList();
    }
    public int registeredDriverCount() {
        return registeredDrivers.size();
    }
}
