package de.julianweinelt.datacat.dbx.api.exceptions;

public class InvalidColorException extends RuntimeException {
    public InvalidColorException(String color) {
        super("The color String " + color + " does not match the pattern rrr;ggg;bbb;aaa");
    }
}
