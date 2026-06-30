package de.julianweinelt.datacat.dbx.api.ui.theme;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.julianweinelt.datacat.dbx.api.plugins.DbxPlugin;
import lombok.Getter;

import javax.swing.plaf.basic.BasicLookAndFeel;

@Getter
public class Theme {
    private final DbxPlugin definingPlugin;
    private final String unlocalizedName;

    private final String themeData;
    private BasicLookAndFeel lafClass = null;

    /**
     * Define a new theme
     * @param definingPlugin An instance of a {@link DbxPlugin} which registers this theme
     * @param unlocalizedName an internal name, cannot contain spaces
     * @param themeData The JSON theme data as a {@link String}
     * @throws IllegalArgumentException if the entered JSON theme data is not valid JSON
     * @deprecated In favor of the new yarn system (since version 1.0.1-beta.1)
     */
    @Deprecated(since = "1.0.1-beta.1")
    public Theme(DbxPlugin definingPlugin, String unlocalizedName, String themeData) throws IllegalArgumentException {
        this.definingPlugin = definingPlugin;
        this.unlocalizedName = unlocalizedName;
        if (!checkJson(themeData)) throw new IllegalArgumentException("Invalid JSON data");
        this.themeData = themeData;
    }

    /**
     * Define a new theme using a Java LAF class
     * @param definingPlugin The {@link DbxPlugin} defining this theme
     * @param unlocalizedName an internal name, cannot contain spaces
     * @param lafClass A {@link BasicLookAndFeel} instance containing all theme data
     * @deprecated In favor of the new yarn system (since version 1.0.1-beta.1)
     */
    @Deprecated(since = "1.0.1-beta.1")
    public Theme(DbxPlugin definingPlugin, String unlocalizedName, BasicLookAndFeel lafClass) {
        this.definingPlugin = definingPlugin;
        this.unlocalizedName = unlocalizedName;
        this.themeData = "";
        this.lafClass = lafClass;
    }

    private boolean checkJson(String input) {
        try {
            JsonParser.parseString(input);
            return true;
        } catch (JsonParseException ignored) {
            return false;
        }
    }
}