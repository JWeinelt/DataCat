package de.julianweinelt.datacat.dbx.api.plugins.yarn;

import de.julianweinelt.datacat.dbx.api.Registry;
import de.julianweinelt.datacat.dbx.api.ui.theme.Theme;
import de.julianweinelt.datacat.dbx.util.FileUtil;
import lombok.Getter;

import java.io.File;

@Getter
public class Yarn {
    private final FileManifest fileManifest;
    private final YarnManifest yarnManifest;
    private final File yarnBase;

    public Yarn(FileManifest fileManifest, YarnManifest yarnManifest) {
        this.fileManifest = fileManifest;
        this.yarnManifest = yarnManifest;
        yarnBase = new File(new File("yarns", ".remapped"), yarnManifest.getName());
    }

    public void registerThemes() {
        File[] files = new File(yarnBase, "themes").listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.getName().endsWith(".theme.json")) continue;
            String content = FileUtil.readFile(file);
            Theme theme = new Theme(Registry.instance().getApi().getSystemPlugin(),
                    file.getName().replace(".theme.json", ""), content);
            Registry.instance().registerTheme(theme);
        }
    }
}