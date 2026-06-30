package de.julianweinelt.datacat.dbx.api.ui.components;

import de.julianweinelt.datacat.dbx.api.ui.Component;
import de.julianweinelt.datacat.dbx.api.ui.ComponentType;

import javax.swing.*;

public class ComponentHorizontalLine extends Component<Void, ComponentHorizontalLine> {

    private final JSeparator separator;

    public ComponentHorizontalLine() {
        super(ComponentType.SEPARATOR);
        separator = new JSeparator(SwingConstants.HORIZONTAL);
    }

    @Override
    public boolean hasLabel() {
        return false;
    }

    @Override
    public boolean expandHorizontally() {
        return true;
    }

    @Override
    public ComponentHorizontalLine initialValue(Object val) {
        return this;
    }

    @Override
    public Void value() {
        return null;
    }

    @Override
    public JComponent create() {
        return separator;
    }
}