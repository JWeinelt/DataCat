package de.julianweinelt.datacat.dbx.startup;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@Getter
public class ArgumentValue extends Argument {
    private String value;
    private Pattern pattern;
    private boolean patternRequired;
    private String valueDescription;

    public ArgumentValue(String baseName, String value) {
        super(baseName, ArgumentType.ARGUMENT_PROPERTY);
        this.value = value;
    }

    public ArgumentValue pattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public ArgumentValue patternRequired(boolean required) {
        this.patternRequired = required;
        return this;
    }

    public ArgumentValue description(String description) {
        this.valueDescription = description;
        return this;
    }

    @Override
    public @Nullable String value() {
        return value;
    }
}
