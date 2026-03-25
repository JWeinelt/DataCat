package de.julianweinelt.databench.ui;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public final class KeyStrokeUtils {

    public static String toString(KeyStroke ks) {
        if (ks == null) return "";
        return InputEvent.getModifiersExText(ks.getModifiers()) +
                (ks.getModifiers() == 0 ? "" : "+") +
                KeyEvent.getKeyText(ks.getKeyCode());
    }
}
