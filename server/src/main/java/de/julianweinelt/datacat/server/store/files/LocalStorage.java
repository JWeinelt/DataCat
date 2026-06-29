package de.julianweinelt.datacat.server.store.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class LocalStorage {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static LocalStorage instance;
    private final File configFile;
    @Getter
    private Configuration config = new Configuration();

    public LocalStorage() {
        instance = this;

        configFile = new File("config.json");

        loadConfig();
        createDefaultHikariOptions();
    }

    public static LocalStorage instance() {
        return instance;
    }

    public void createDefaultHikariOptions() {
        File f = new File("hikari.properties");
        if (f.exists()) return;
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource");
        props.setProperty("dataSource.user", "datacat-store");
        props.setProperty("dataSource.password", "secret");
        props.setProperty("dataSource.serverName", "10.10.20.1");
        props.setProperty("dataSource.portNumber", "3306");
        props.setProperty("dataSource.databaseName", "datacat");
        try (OutputStream os = new FileOutputStream(f)) {
            props.store(os, null);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void saveConfig() {
        try (FileWriter w = new FileWriter(configFile)) {
            w.write(GSON.toJson(config));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    public void loadConfig() {
        if (!configFile.exists()) {
            saveConfig();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            config = GSON.fromJson(br, Configuration.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
