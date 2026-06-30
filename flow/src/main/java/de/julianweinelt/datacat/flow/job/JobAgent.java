package de.julianweinelt.datacat.flow.job;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class JobAgent {
    private final Gson GSON = new Gson();
    private static JobAgent instance;
    private final File jobFolder;

    public static JobAgent instance() {
        return instance;
    }
    public JobAgent() {
        instance = this;
        jobFolder = new File("jobs");
        if (jobFolder.mkdirs()) log.debug("Jobs folder created");
    }
}