package de.julianweinelt.datacat.server.store.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.julianweinelt.datacat.server.store.account.*;
import de.julianweinelt.datacat.server.util.ColorUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class DBStorage {
    private static DBStorage instance;

    private final HikariConfig config;
    private final HikariDataSource dS;


    public DBStorage() {
        instance = this;
        config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setUsername("root");
        config.setPassword("root");
        config.setJdbcUrl("jdbc:mariadb://localhost:3306/datacat-store");
        config.setMaximumPoolSize(10);
        dS = new HikariDataSource(config);
    }

    public static DBStorage instance() {
        return instance;
    }

    public void loadMetaData() {
        try (PreparedStatement pS = dS.getConnection().prepareStatement("SELECT StatusID, StatusName, Color FROM accounts_account_statuses")) {
            ResultSet set = pS.executeQuery();
            while (set.next()) {
                AccountStatus status = new AccountStatus(UUID.fromString(set.getString(1)), set.getString(2),
                        ColorUtil.fromText(set.getString(3)));
                status.register();
            }
        } catch (SQLException e) {
            log.error("Error while loading account statuses from database: {}", e.getMessage(), e);
        }

        checkAndCreateDefaultAccountStatuses();

        try (PreparedStatement pS = dS.getConnection().prepareStatement("SELECT StatusID, StatusName, Color FROM accounts_verification_statuses")) {
            ResultSet set = pS.executeQuery();
            while (set.next()) {
                VerificationStatus status = new VerificationStatus(UUID.fromString(set.getString(1)), set.getString(2),
                        ColorUtil.fromText(set.getString(3)));
                log.info("Loaded verification status {} ({})", status.id(), status.name());
                status.register();
            }
        } catch (SQLException e) {
            log.error("Error while loading verification statuses from database: {}", e.getMessage(), e);
        }

        checkAndCreateDefaultVerificationStatuses();

        try (PreparedStatement pS = dS.getConnection().prepareStatement("SELECT LinkID, DisplayName, IconName, UrlRegex FROM link_type")) {
            ResultSet set = pS.executeQuery();
            while (set.next()) {
                AccountLinkType type = new AccountLinkType(UUID.fromString(set.getString(1)), set.getString(2),
                        set.getString(3), set.getString(4));
                type.register();
            }
        } catch (SQLException e) {
            log.error("Error while loading account link types from database: {}", e.getMessage(), e);
        }
    }

    public CompletableFuture<Account> loadUser(String value, LoadMethod method) {
        String sql = "SELECT UserID, eMail, Username, PasswordHashed, Created, LastLogin, AccountStatus, VerificationStatus FROM accounts WHERE ";
        switch (method) {
            case USERNAME -> sql += "username = ?";
            case EMAIL -> sql += "eMail = ?";
            case UUID -> sql += "UserID = ?";
        }
        try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
            pS.setString(1, value);

            ResultSet set = pS.executeQuery();
            if (set.next()) {
                Account account = new Account(
                        UUID.fromString(set.getString(1)),
                        set.getString(3),
                        set.getString(2),
                        set.getString(4),
                        set.getLong(5),
                        set.getLong(6),
                        AccountStatus.get(UUID.fromString(set.getString(7))),
                        VerificationStatus.get(UUID.fromString(set.getString(8)))
                );
                account.setPermissions(loadPermissions(account.getUniqueId()));
                account.register();
                return CompletableFuture.completedFuture(account);
            }
        } catch (SQLException e) {
            log.error("Error while loading user from database: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<java.util.List<AccountLink>> getAccountLinks(UUID account) {
        String sql = "SELECT LinkUrl, LinkID FROM account_links WHERE AccountID = ?";

        List<AccountLink> links = new ArrayList<>();

        try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
            pS.setString(1, account.toString());

            ResultSet set = pS.executeQuery();
            while (set.next()) {
                links.add(new AccountLink(account, AccountLinkType.get(UUID.fromString(set.getString(2))), set.getString(1)));
            }

            return CompletableFuture.completedFuture(links);
        } catch (SQLException e) {
            log.error("Error while loading account links from database: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private AccountPermissions loadPermissions(UUID uuid) {
        String sql = "SELECT PermissionName FROM account_permissions WHERE AccountID = ?";
        try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
            pS.setString(1, uuid.toString());
            ResultSet set = pS.executeQuery();

            AccountPermissions permissions = new AccountPermissions(uuid);
            while (set.next()) permissions.addPermission(set.getString(1));
            return permissions;
        } catch (SQLException e) {
            log.error("Error while loading permissions from database: {}", e.getMessage(), e);
            return new AccountPermissions(uuid);
        }
    }

    private void checkAndCreateDefaultAccountStatuses() {
        if (!AccountStatus.getAll().isEmpty()) return;
        new AccountStatus(UUID.randomUUID(), "created", Color.GRAY).register();
        new AccountStatus(UUID.randomUUID(), "registered", Color.GREEN).register();
        new AccountStatus(UUID.randomUUID(), "inactive", Color.YELLOW).register();
        new AccountStatus(UUID.randomUUID(), "banned", Color.RED).register();

        sendAccountStatusesToDB();
    }

    private void sendAccountStatusesToDB() {
        String sql = "INSERT INTO accounts_account_statuses (StatusID, StatusName, Color) VALUES (?, ?, ?)";
        for (AccountStatus status : AccountStatus.getAll()) {
            try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
                pS.setString(1, status.id().toString());
                pS.setString(2, status.name());
                pS.setString(3, ColorUtil.toText(status.color()));
                pS.execute();
            } catch (SQLException e) {
                log.error("Error while sending account status to database: {}", e.getMessage(), e);
            }
        }
    }

    private void checkAndCreateDefaultVerificationStatuses() {
        if (!VerificationStatus.getAll().isEmpty()) return;
        new VerificationStatus(UUID.randomUUID(), "unverified", Color.GRAY).register();
        new VerificationStatus(UUID.randomUUID(), "pending", Color.YELLOW).register();
        new VerificationStatus(UUID.randomUUID(), "verified", Color.GREEN).register();

        sendVerificationStatusesToDB();
    }

    private void sendVerificationStatusesToDB() {
        String sql = "INSERT INTO accounts_verification_statuses (StatusID, StatusName, Color) VALUES (?, ?, ?)";
        for (VerificationStatus status : VerificationStatus.getAll()) {
            try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
                pS.setString(1, status.id().toString());
                pS.setString(2, status.name());
                pS.setString(3, ColorUtil.toText(status.color()));
                pS.execute();
            } catch (SQLException e) {
                log.error("Error while sending verification status to database: {}", e.getMessage(), e);
            }
        }
    }

    public void updateAccount(Account account) {
        String sql = """
                INSERT INTO accounts (UserID, eMail, Username, PasswordHashed, Created, LastLogin, AccountStatus, VerificationStatus)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE eMail = ?, Username = ?, PasswordHashed = ?, LastLogin = ?, AccountStatus = ?, VerificationStatus = ?
                """;

        try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
            pS.setString(1, account.getUniqueId().toString());
            pS.setString(2, account.getEmail());
            pS.setString(3, account.getUsername());
            pS.setString(4, account.getPasswordHash());
            pS.setLong(5, account.getCreationTime());
            pS.setLong(6, account.getLastLogin());
            pS.setString(7, account.getAccountStatus().id().toString());
            pS.setString(8, account.getVerificationStatus().id().toString());
            pS.setString(9, account.getEmail());
            pS.setString(10, account.getUsername());
            pS.setString(11, account.getPasswordHash());
            pS.setLong(12, account.getLastLogin());
            pS.setString(13, account.getAccountStatus().id().toString());
            pS.setString(14, account.getVerificationStatus().id().toString());

            pS.execute();
        } catch (SQLException e) {
            log.error("Error while updating account in database: {}", e.getMessage(), e);
        }
    }

    public void updateAccountLink(AccountLink link) {
        String sql = "INSERT INTO account_links (AccountID, LinkID, LinkUrl) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LinkUrl = ?";
        try (PreparedStatement pS = dS.getConnection().prepareStatement(sql)) {
            pS.setString(1, link.account().toString());
            pS.setString(2, link.type().id().toString());
            pS.setString(3, link.value());
            pS.setString(4, link.value());

            pS.execute();
        } catch (SQLException e) {
            log.error("Error while updating account link in database: {}", e.getMessage(), e);
        }
    }


    public enum LoadMethod {
        USERNAME,
        EMAIL,
        UUID
    }
}