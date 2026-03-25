package de.julianweinelt.datacat.ui.driver;

import de.julianweinelt.datacat.dbx.api.drivers.DriverShim;

import javax.swing.*;
import java.awt.*;
import java.sql.Driver;

public class DriverListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Driver driver) {

            Driver realDriver = driver;
            if (driver instanceof DriverShim shim) {
                realDriver = shim.getDelegate();
            }

            String simpleName = realDriver.getClass().getSimpleName();
            String name = realDriver.getClass().getName();

            setText(simpleName);
            setToolTipText(name);
        }

        return this;
    }
}
