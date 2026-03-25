package de.julianweinelt.datacat.launcher.setup;

import javax.swing.*;

public interface WizardPage {

    String getId();
    JComponent getView();

    default void onEnter() {}
    default void onLeave() {}

    boolean canGoNext();
}
