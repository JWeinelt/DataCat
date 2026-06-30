package de.julianweinelt.datacat.dbx.startup;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class Argument {
    private final String baseName;
    private final ArgumentType type;

    public Argument(String baseName, ArgumentType type) {
        this.baseName = baseName;
        this.type = type;
    }

    @Nullable
    public abstract String value();
}