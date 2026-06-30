package de.julianweinelt.datacat.dbx.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Class for defining the DBX export manifest data.
 * @author Julian Weinelt
 * @version 1.0.0
 * @since 0.0.1
 */
@Getter
public final class Manifest {
    private final String formatVersion = "1.0.0";
    private long createdAt;

    private Tool tool;
    private Source source;
    private Export export;

    public static Manifest create(String version, String dbms, String dbmsVersion, List<String> databases) {
        Manifest manifest = new Manifest();
        manifest.createdAt = System.currentTimeMillis();
        manifest.tool = Tool.create(version);
        manifest.source = new Source(dbms, dbmsVersion);
        manifest.export = new Export(databases);
        return manifest;
    }

    @Getter
    public static final class Tool {
        private final String name;
        private final String version;

        Tool(String name, String version) {
            this.name = name;
            this.version = version;
        }

        static Tool create(String version) {
            return new Tool("DataCat", version);
        }
    }

    public record Source(String dbms, String dbmsVersion) {}

    @Getter
    public static final class Export {
        private final String mode = "DBX";
        private final List<String> databases;

        public Export(List<String> databases) {
            this.databases = databases;
        }
        public Export(String... databases) {
            this.databases = Arrays.asList(databases);
        }
    }
}