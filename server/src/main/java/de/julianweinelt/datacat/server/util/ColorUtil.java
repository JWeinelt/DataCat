package de.julianweinelt.datacat.server.util;

import java.awt.*;

public class ColorUtil {
    public static int toRGB(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static Color fromText(String text) {
        String[] data = text.trim().split(";");
        if (data.length == 3) return new Color(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
        if (data.length == 4) return new Color(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        throw new IllegalArgumentException("Invalid color format: " + text);
    }
    public static String toText(Color color) {
        return color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";" + color.getAlpha();
    }
}
