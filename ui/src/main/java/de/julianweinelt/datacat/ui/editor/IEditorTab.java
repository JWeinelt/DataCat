package de.julianweinelt.datacat.ui.editor;

import de.julianweinelt.datacat.api.DConnection;
import de.julianweinelt.datacat.ui.BenchUI;

import javax.swing.*;
import java.util.UUID;

public interface IEditorTab {
    UUID getId();

    JPanel getTabComponent(BenchUI ui, DConnection connection);
    String getTitle();
}