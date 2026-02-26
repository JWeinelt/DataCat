package de.julianweinelt.databench.dbx.api.ui.tree;

import javax.swing.*;

public interface TreeContextAction {

    String getName();

    Icon getIcon();

    void execute(TreeNode node);
}