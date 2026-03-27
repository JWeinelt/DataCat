package de.julianweinelt.datacat.dbx.api.ui;


public enum ComponentType {
    /**
     * A standard text-driven component for entering text
     */
    TEXT,
    /**
     * A component for storing boolean values
     */
    CHECKBOX,
    /**
     * A component for storing numbers
     */
    NUMBER,
    /**
     * A component for making selection out of a list
     */
    COMBOBOX,
    /**
     * A component for buttons, executing an action on press
     */
    BUTTON,
    /**
     * A component just to write some text on screen
     */
    LABEL,
    /**
     * A component for selecting colors
     */
    COLOR,
    /**
     * A component for separating
     */
    SEPARATOR;
}