package de.julianweinelt.datacat.dbx.util;

import de.julianweinelt.datacat.dbx.api.exceptions.InvalidColorException;

import java.awt.*;
import java.util.regex.Pattern;


/**
 * Utility class for parsing colors. Colors are converted in a database-friendly String format:<br>
 * <code>rrr;ggg;bbb;aaa</code>
 * @author Julian Weinelt
 * @version 1.0.0
 */
public class ColorUtil {
    private static final Pattern RGBA_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d);" +
                    "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d);" +
                    "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d);" +
                    "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
    );

    /**
     * Convert a {@link Color} to a {@link String}
     * @param color The {@link Color} object to convert
     * @return The color in the text format <code>rrr;ggg;bbb;aaa</code>
     */
    public static String toString(Color color) {
        return color.getRed() + ";" + color.getGreen() +
                ";" + color.getBlue() + ";" + color.getAlpha();
    }

    /**
     * Convert a text into a {@link Color} using the format <code>rrr;ggg;bbb;aaa</code>
     * @apiNote This method also checks if the entered string is a valid color.
     * @param color The {@link String} to convert
     * @return A {@link Color} object created from the text
     * @throws InvalidColorException if the entered text cannot be parsed to a color.
     */
    public static Color toColor(String color) {
        if (!isValidRGBA(color)) throw new InvalidColorException(color);
        String[] args = color.split(";");
        int red = Integer.parseInt(args[0]);
        int green = Integer.parseInt(args[1]);
        int blue = Integer.parseInt(args[2]);
        if (args.length == 3) {
            return new Color(red, green, blue);
        } else if (args.length == 4) {
            int alpha = Integer.parseInt(args[3]);
            return new Color(red, green, blue, alpha);
        } else return null;
    }

    public static boolean isValidRGBA(String input) {
        return RGBA_PATTERN.matcher(input).matches();
    }
}
