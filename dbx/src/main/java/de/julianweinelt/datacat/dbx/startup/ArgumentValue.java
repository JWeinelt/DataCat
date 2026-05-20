package de.julianweinelt.datacat.dbx.startup;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class ArgumentValue extends Argument {
    private String value;
    private Pattern pattern;
    private boolean patternRequired;
    private String valueDescription;

    public ArgumentValue(String baseName, ArgumentType type, String value) {
        super(baseName, type);
    }

    @Override
    public @Nullable String value() {
        return value;
    }
}
