package de.julianweinelt.datacat.dbx.startup;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class StartArguments {
    @Getter
    private final List<String> arguments;

    private StartArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public static StartArguments of(String[] args) {
        return new StartArguments(Arrays.stream(args).toList());
    }
}