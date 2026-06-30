package de.julianweinelt.datacat.dbx.api.exceptions;

public class NoDriverFoundException extends RuntimeException {
    public NoDriverFoundException(String database) {
        super("A driver for the database of type " + database + " could not be found.");
    }
}
