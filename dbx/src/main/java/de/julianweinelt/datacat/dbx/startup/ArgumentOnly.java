package de.julianweinelt.datacat.dbx.startup;

import org.jetbrains.annotations.Nullable;

public class ArgumentOnly extends Argument {
    public ArgumentOnly(String baseName, ArgumentType type) {
        super(baseName, type);
    }

    @Override
    public @Nullable String value() {
        return null;
    }
}