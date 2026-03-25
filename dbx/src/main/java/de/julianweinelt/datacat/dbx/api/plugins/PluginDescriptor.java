package de.julianweinelt.datacat.dbx.api.plugins;

import java.io.File;
import java.net.URL;

public record PluginDescriptor(String name, File jarFile, URL jarUrl, PluginConfiguration config) {}