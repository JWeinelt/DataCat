package de.julianweinelt.datacat.dbx.api.nativelib;

import de.julianweinelt.datacat.dbx.api.Registry;
import de.julianweinelt.datacat.dbx.api.plugins.DbxPlugin;
import de.julianweinelt.datacat.dbx.api.ui.UIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class NativeLibManager {
    private static NativeLibManager instance;

    public static NativeLibManager instance() {
        return instance;
    }

    public NativeLibManager(DbxPlugin systemPlugin) {
        instance = this;

        Registry.instance().registerEvents(systemPlugin,
                "NativeLibraryLoadedEvent", "NativeLibraryRegisterEvent", "NativeLibraryRequestEvent",
                "NativeLibraryUserEvent");
    }

    private final List<NativeLibrary> libraries = new ArrayList<>();

    public void requestLibraries(DbxPlugin source, boolean require, NativeLibrary... libs) {
        List<String> names = new ArrayList<>();
        for (NativeLibrary lib : libs) {
            names.add(lib.name());
        }
        UIService.instance().openDialogResult("de.julianweinelt.datacat.ui.nativelib.NativeLibraryDialog",
                names, source, require).thenAccept(result -> {
            if (result == 0 || result == 1) {
                //TODO: Add Plugin as trusted source
                Arrays.stream(libs).forEach(this::downloadLibrary);
                int res = JOptionPane.showConfirmDialog(null, "Installed native libraries cannot be loaded" +
                        " while DataCat is running. All changes will take effect after a restart. Restart now?", "Restart required", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public void downloadLibrary(NativeLibrary library) {
        LibraryPlatform platform = LibraryPlatform.WINDOWS;

        String u = library.downloadUrl(platform, "");
        if (u == null) {
            throw new RuntimeException("No download URL found for " + library.name());
        }
        try {
            URL url = new URL(u);

            File targetPath = new File("lib", library.name() + "." + platform.fileEnding);

            FileUtils.copyURLToFile(url, targetPath, 5000, 10000);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid download URL for " + library.name(), e);
        } catch (IOException e) {
            log.error("Could not download library {}", library.name(), e);
        }
    }

    public void registerLibrary(NativeLibrary library, DbxPlugin source) {

    }
}