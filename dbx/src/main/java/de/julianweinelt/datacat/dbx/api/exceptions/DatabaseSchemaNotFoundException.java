package de.julianweinelt.datacat.dbx.api.exceptions;

public class DatabaseSchemaNotFoundException extends RuntimeException {
    public DatabaseSchemaNotFoundException(String database) {
        super("The schema {db} could not be found on this database server.".replace("{db}", database));
    }
}
