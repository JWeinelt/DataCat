package de.julianweinelt.datacat.server.store.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.password4j.Password;
import de.julianweinelt.datacat.server.store.StoreServer;
import de.julianweinelt.datacat.server.store.database.DBStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class AccountManager {
    private static AccountManager instance;
    public static AccountManager instance() {
        return instance;
    }

    public AccountManager() {
        instance = this;
    }

    private final List<Account> accountsLoaded = new ArrayList<>();
    private final Map<UUID, List<AccountLink>> accountLinks = new HashMap<>();

    public List<AccountLink> retrieveAccountLinks(Account account) {
        if (accountLinks.containsKey(account.getUniqueId())) return accountLinks.get(account.getUniqueId());
        List<AccountLink> dat = DBStorage.instance().getAccountLinks(account.getUniqueId()).join();
        accountLinks.put(account.getUniqueId(), dat);
        return dat;
    }

    public JsonArray getAccountLinkJS(Account a) {
        JsonArray array = new JsonArray();

        List<AccountLink> links = retrieveAccountLinks(a);

        for (AccountLinkType t : AccountLinkType.getAll()) {
            boolean added = false;
            for (AccountLink l : links) {
                if (l.type().equals(t)) {
                    JsonObject o = new JsonObject();
                    o.addProperty("name", t.displayName());
                    o.addProperty("enabled", true);
                    o.add("data", l.toJson());
                    array.add(o);
                    added = true;
                }
            }
            if (added) continue;
            JsonObject o = new JsonObject();
            o.addProperty("name", t.displayName());
            o.addProperty("enabled", false);
            o.add("data", new JsonObject());
            array.add(o);
        }
        return array;
    }

    public void updateAccountLink(Account a, AccountLinkType type, String link) {
        if (!accountLinks.containsKey(a.getUniqueId())) accountLinks.put(a.getUniqueId(), new ArrayList<>());

        for (AccountLink l : accountLinks.getOrDefault(a.getUniqueId(), new ArrayList<>())) {
            if (l.type().equals(type)) accountLinks.get(a.getUniqueId()).remove(l);
        }

        AccountLink e = new AccountLink(a.getUniqueId(), type, link);
        accountLinks.get(a.getUniqueId()).add(e);
        DBStorage.instance().updateAccountLink(e);
    }

    public Account getAccount(String value, DBStorage.LoadMethod method) {
        for (Account account : accountsLoaded) {
            if (method.equals(DBStorage.LoadMethod.UUID) && account.getUniqueId().equals(UUID.fromString(value))) return account;
            if (method.equals(DBStorage.LoadMethod.USERNAME) && account.getUsername().equals(value)) return account;
            if (method.equals(DBStorage.LoadMethod.EMAIL) && account.getEmail().equals(value)) return account;
        }

        Account fromDB = DBStorage.instance().loadUser(value, method).join();
        if (fromDB != null) {
            addAccount(fromDB);
            return fromDB;
        }

        return null;
    }
    public void addAccount(Account account) {
        accountsLoaded.add(account);
    }

    public AccountCreationResponse createAccount(String username, String email, String password) {
        if (getAccount(username, DBStorage.LoadMethod.USERNAME) != null) {
            return AccountCreationResponse.USERNAME_ALREADY_EXISTS;
        }
        if (getAccount(email, DBStorage.LoadMethod.EMAIL) != null) {
            return AccountCreationResponse.EMAIL_ALREADY_EXISTS;
        }

        Account account = new Account(UUID.randomUUID(), username, email, Password.hash(password).withArgon2().getResult(),
                System.currentTimeMillis(), System.currentTimeMillis(), AccountStatus.get("created"), VerificationStatus.get("unverified"));
        addAccount(account);
        DBStorage.instance().updateAccount(account);

        return AccountCreationResponse.CREATED;
    }
}
