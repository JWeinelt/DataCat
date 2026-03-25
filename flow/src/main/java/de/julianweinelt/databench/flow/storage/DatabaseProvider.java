package de.julianweinelt.databench.flow.storage;

import de.julianweinelt.databench.dbx.api.drivers.DriverManagerService;
import de.julianweinelt.databench.dbx.database.DatabaseMetaData;
import de.julianweinelt.databench.dbx.database.DatabaseRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DatabaseProvider {
    @Getter
    private Connection conn;
    private DatabaseMetaData typeMeta;

    public void start() {
        log.info("Initializing database connection...");
        typeMeta = DatabaseRegistry.instance().getMeta(Configuration.instance().getDbType());

    }

    private boolean connect() {
        log.info("Connecting to database...");
        Thread current = Thread.currentThread();
        ClassLoader previous = current.getContextClassLoader();
        current.setContextClassLoader(DriverManagerService.instance().getDriverLoader());

        try {
            String DB_HOST = Configuration.instance().getDbHost() + ":" + Configuration.instance().getDbPort();
            String DB_URL = typeMeta.jdbcURL().replace("${server}", DB_HOST)
                    .replace("${database}", "")
                    .replace("${parameters}", typeMeta.parameters(typeMeta.defaultParameters().build()));

            conn = DriverManager.getConnection(DB_URL, Configuration.instance().getDbUser(), Configuration.instance().getDbPassword());
            log.info("Connected to database!");
            createBase();
            return true;
        } catch (Exception e) {
            log.error("Failed to connect to database: {}", e.getMessage(), e);
        } finally {
            current.setContextClassLoader(previous);
        }
        return false;
    }

    private boolean checkConnection() {
        try {
            if (conn == null || conn.isClosed()) return connect();
            return true;
        } catch (SQLException e) {
            return connect();
        }
    }
    private void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean executeStatement(String statement) {
        try (Statement st = conn.createStatement()) {
            st.execute(statement);
            return true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private void createBase() {
        copyBaseScript();
        String script = loadBaseScript();
        if (script == null) {
            log.warn("Failed to load base script");
            return;
        }
        log.info("Deleting setup.sql...");
        if (new File("setup.sql").delete()) log.info("Done.");
        else log.warn("Failed to delete setup.sql");
        log.info("Setup Script is at version {}", extractVersion(script));
        List<String> statements = parseScript(script);
        int successes = 0;
        try (ProgressBar pb = new ProgressBar("Setting up database...", statements.size())) {
            for (String st : statements) {
                if (executeStatement(st)) successes++;
                pb.step();
            }
        }
        log.info("Executed all statements. Checking integrity...");
        log.info("{} of {} statements executed successfully.", successes, statements.size());
        if (successes != statements.size()) {
            log.warn("Some statements failed. Please check the log for details.");
            //TODO: Further testing
        } else {
            log.info("Database setup completed successfully!");
        }
    }

    private void copyBaseScript() {
        try (InputStream src = getClass().getResourceAsStream("/flow.sql")) {
            File target = new File("setup.sql");
            if (src == null) return;
            Files.copy(src, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied base script to {}", target.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to copy base script for database creation", e);
        }
    }
    private String loadBaseScript() {
        try (BufferedReader br = new BufferedReader(new FileReader("setup.sql"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String extractVersion(String script) {
        Pattern pattern = Pattern.compile("VERSION:([0-9]+(?:\\.[0-9]+)*)");
        Matcher matcher = pattern.matcher(script);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.warn("Could not extract version from script");
            return null;
        }
    }
    private List<String> parseScript(String script) {
        log.info("Parsing setup script...");
        StringBuilder sb = new StringBuilder();
        String[] lines = script.split("\n");
        for (String line : lines) {
            if (line.startsWith("--")) continue;
            if (line.trim().isEmpty()) continue;
            sb.append(line.trim());
        }
        return List.of(sb.toString().split(";"));
    }
}
