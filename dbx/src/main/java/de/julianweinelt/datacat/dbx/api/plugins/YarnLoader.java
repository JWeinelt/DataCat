package de.julianweinelt.datacat.dbx.api.plugins;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.julianweinelt.datacat.dbx.api.DbxAPI;
import de.julianweinelt.datacat.dbx.api.plugins.yarn.FileManifest;
import de.julianweinelt.datacat.dbx.api.plugins.yarn.Yarn;
import de.julianweinelt.datacat.dbx.api.plugins.yarn.YarnManifest;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class YarnLoader {
    private final File baseFolder = new File("yarns");
    private final File remappedFolder = new File(baseFolder, ".remapped");

    public YarnLoader() {
        if (baseFolder.mkdirs()) log.debug("Created yarn base folder");
        if (remappedFolder.mkdirs()) log.debug("Created yarn remapped folder");
        discoverYarns();
    }

    public void discoverYarns() {
        File[] files = baseFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yarn")) {
                loadYarn(file);
            }
        }
    }

    public void loadYarn(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            log.info("Loading yarn {}", file.getName());
            YarnManifest manifest = loadYarnManifest(zipFile);
            FileManifest fileManifest = loadFileManifest(zipFile);
            if (new File(remappedFolder, manifest.getName()).mkdirs()) {
                log.debug("Created yarn remapped folder for {}", manifest.getName());
            }
            log.info("Copying theme data from yarn {}", manifest.getName());
            copyThemeData(zipFile, manifest.getName());
            loadPlugins(zipFile);

            Yarn yarn = new Yarn(fileManifest, manifest);
            yarn.registerThemes();
            log.info("Finished loading yarn {}", manifest.getName());
        } catch (IOException e) {
            log.error("Failed to load yarn", e);
        }
    }

    private void loadPlugins(ZipFile file) {
        ZipEntry entry = file.getEntry("plugin.json");
        try (InputStream iS = file.getInputStream(entry)) {
            String json = new String(iS.readAllBytes(), StandardCharsets.UTF_8);

            JsonArray a = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement e : a) {
                String name = e.getAsJsonObject().get("name").getAsString();
                String version = e.getAsJsonObject().get("version").getAsString();
                log.info("Loading plugin {} v{} from yarn", name, version);
                copyPluginToTemp(file, name, version);
            }
        } catch (IOException e) {
            log.error("Failed to load plugin data", e);
        }
    }

    private void copyPluginToTemp(ZipFile file, String pluginName, String version) {
        File target = new File(DbxAPI.pluginsFolder(), pluginName + ".yarn.jar");

        ZipEntry entry = file.getEntry("datacat/" + pluginName + "-" + version + ".jar");
        try (InputStream iS = file.getInputStream(entry)) {
            Files.copy(iS, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to copy plugin to temp folder", e);
        }
    }

    private FileManifest loadFileManifest(ZipFile file) {
        return loadFile(file, FileManifest.class, "manifest.json");
    }
    private YarnManifest loadYarnManifest(ZipFile file) {
        return loadFile(file, YarnManifest.class, "yarn.json");
    }

    private <T> T loadFile(ZipFile file, Class<T> clazz, String entryName) {
        ZipEntry entry = file.getEntry(entryName);
        if (entry == null) return null;
        try (InputStream iS = file.getInputStream(entry)) {
            String json = new String(iS.readAllBytes(), StandardCharsets.UTF_8);
            return new Gson().fromJson(json, clazz);
        } catch (IOException e) {
            log.error("Failed to load yarn manifest", e);
            return null;
        }
    }

    private void copyThemeData(ZipFile file, String yarnName) throws IOException {
        File targetFolder = new File(new File(remappedFolder, yarnName), "themes");
        if (targetFolder.mkdirs()) log.debug("Created yarn theme folder for {}", yarnName);
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory() && !entry.getName().startsWith("themes/")) continue;

            File targetFile = new File(targetFolder, entry.getName().substring("themes/".length()));
            File parent = targetFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory " + parent);
            }

            try (InputStream in = file.getInputStream(entry);
                    OutputStream out = new FileOutputStream(targetFile)) {
                in.transferTo(out);
            }
        }
    }
}