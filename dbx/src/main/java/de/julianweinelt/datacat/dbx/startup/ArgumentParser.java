package de.julianweinelt.datacat.dbx.startup;

public interface ArgumentParser {
    Argument parse(StartArguments args, int index);
}