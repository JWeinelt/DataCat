package de.julianweinelt.datacat.dbx.api.drivers;

import de.julianweinelt.datacat.dbx.api.VersionStatus;
import de.julianweinelt.datacat.dbx.util.LanguageManager;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class PluginDriver {
    @Getter
    private final String internalName;

    @Getter
    private final List<DriverVersion> availableVersions = new CopyOnWriteArrayList<>();

    public PluginDriver(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Gets the latest version of the driver by looking it up on the internet.
     * @return The latest version as {@link String}, mostly in a semantic version
     *
     * @implNote The version should be fetched from a service containing the downloads for this driver, e.g. GitHub
     */
    public abstract CompletableFuture<String> latestVersion();

    /**
     * Defines the url to download the driver jar from depending on the given version.
     * @param version The version String which will be used in the download
     * @return A {@link String} containing the url to download the file from
     */
    public abstract String downloadURL(String version);

    /**
     * Defines if the downloaded driver files are usually compressed in e.g. zip or tar.
     * @return <code>true</code> if the driver files are usually compressed, otherwise <code>false</code>
     *
     * @implNote Override this method if you want to tell DataCat to decompress files after download.
     * @see #archiveType()
     */
    public boolean isZippedFile() {
        return false;
    }

    /**
     * Defines which type of archive this driver is.
     * @return A {@link String} containing the file extension of the compressed files, <code>null</code> if {@link #isZippedFile()} is <code>false</code>
     *
     * @implNote Override this method only if you set {@link #isZippedFile()} to <code>true.</code>
     */
    public @Nullable String archiveType() {
        return null;
    }


    /**
     * Define every version available for this driver here.<br>
     * You may use {@link #addVersion(String)} in this method to define versions.
     *
     * @implNote You may use asynchronous requests here.
     * @apiNote This method should not be called much, as it may perform asynchronous HTTP requests, depending on the implementation.
     * @see #addVersion(String, boolean, VersionStatus)
     */
    public abstract void defineVersions();
    public abstract CompletableFuture<Void> defineVersionsASync();

    /**
     * Registers a new version for this driver, giving just the name.<br>
     * The version will therefore be registered as semantic version of type {@link VersionStatus.RELEASE}.
     * @param versionName The name of the version as a {@link String}, e.g. <code>1.4.2</code> (semantic) or <code>1.7.542.3-beta-1</code> (non-semantic)
     * @see #addVersion(String, boolean, VersionStatus)
     *
     * @apiNote This method is thread-safe.
     */
    protected void addVersion(String versionName) {
        availableVersions.add(new DriverVersion(versionName, true, VersionStatus.RELEASE));
    }

    /**
     * Registers a new version for this driver, giving just the name.<br>
     * The version will therefore be registered as a version of type {@link VersionStatus.RELEASE}.
     * @param versionName The name of the version as a {@link String}, e.g. <code>1.4.2</code> (semantic) or <code>1.7.542.3-beta-1</code> (non-semantic)
     * @param semantic Defines if this is a semantic version
     * @see #addVersion(String, boolean, VersionStatus)
     *
     * @apiNote This method is thread-safe.
     */
    protected void addVersion(String versionName, boolean semantic) {
        availableVersions.add(new DriverVersion(versionName, semantic, VersionStatus.RELEASE));
    }
    /**
     * Registers a new version for this driver, giving just the name.
     * @param versionName The name of the version as a {@link String}, e.g. <code>1.4.2</code> (semantic) or <code>1.7.542.3-beta-1</code> (non-semantic)
     * @param semantic Defines if this is a semantic version
     * @param status The type of version, see {@link VersionStatus} for more.
     * @see #addVersion(String, boolean, VersionStatus)
     *
     * @apiNote This method is thread-safe.
     */
    protected void addVersion(String versionName, boolean semantic, VersionStatus status) {
        availableVersions.add(new DriverVersion(versionName, semantic, status));
    }


    @Override
    public String toString() {
        return LanguageManager.translate("driver.database." + internalName + ".name");
    }
}
