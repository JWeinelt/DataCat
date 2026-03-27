package de.julianweinelt.datacat.dbx.api.ui.menubar;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class Menu extends MenuComponent<JMenu> {

    private final List<MenuComponent> children = new ArrayList<>();
    @Getter
    private int priority = -1;

    @Getter
    private final String categoryName;
    private final String name;

    /**
     * Create a new menu for the menubar in the editor. To define a priority, you can either call the method {@link #priority} or
     * call the constructor with priority {@link #Menu(String, String, int)}
     * @param name A {@link String} containing the name of the menu
     * @param categoryName The internal name of the menu. Only alphanumerical lower case letters and underscores are allowed.
     */
    public Menu(String name, String categoryName) {
        if (!categoryName.matches("[a-z_]")) throw new IllegalArgumentException("Category can only contain lower case letters and underscores.");
        this.categoryName = categoryName;
        this.name = name;
    }

    /**
     * Create a new menu for the menubar in the editor
     * @param name A {@link String} containing the name of the menu
     * @param categoryName The internal name of the menu. Only alphanumerical lower case letters and underscores are allowed.
     * @param priority The display priority. Must be below 900 to not conflict with the default menu.
     */
    public Menu(String name, String categoryName, int priority) {
        if (!categoryName.matches("[a-z_]")) throw new IllegalArgumentException("Category can only contain lower case letters and underscores.");
        this.categoryName = categoryName;
        this.name = name;
        priority(priority);
    }

    /**
     * Defines the display priority of the menu. All numbers greater than 899 are system-reserved.
     * @param priority The priority as an integer
     * @return The creating {@link Menu} object
     * @apiNote You can use this method for chaining.
     */
    public Menu priority(int priority) {
        if (priority == 999) throw new IllegalArgumentException("Priority must be lower than 999");
        this.priority = priority;
        return this;
    }

    /**
     * Adds a new child to the next row in the menu.
     * @param menuComponent A {@link MenuComponent} object, either a sub-menu or a menu item.
     * @return The creating {@link Menu} object
     * @apiNote You can use this method for chaining.
     */
    public Menu child(MenuComponent menuComponent) {
        children.add(menuComponent);
        return this;
    }

    /**
     * Adds a separator child to the next row of the menu. It will be displayed as a horizontal line.
     * @return The creating {@link Menu} object
     * @apiNote You can use this method for chaining.
     */
    public Menu separator() {
        children.add(new MenuSeparator());
        return this;
    }

    @Override
    protected JMenu create() {
        JMenu menu = new JMenu(name);
        menu.removeAll();
        log.debug("Started menu creation");
        int idx = 0;
        for (MenuComponent menuComponent : children) {
            if (menuComponent instanceof MenuSeparator && idx != 0) {
                menu.addSeparator();
                log.debug("Separator");
                continue;
            }
            JMenuItem i = menuComponent.create();
            menu.add(i);

            idx++;
            log.debug("{}", idx);
        }
        log.debug("Created menu {}", getCategoryName());
        return menu;
    }
}