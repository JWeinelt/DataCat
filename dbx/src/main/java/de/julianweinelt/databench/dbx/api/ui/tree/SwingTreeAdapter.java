package de.julianweinelt.databench.dbx.api.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class SwingTreeAdapter {
    public DefaultMutableTreeNode toSwingNode(TreeNode node) {
        DefaultMutableTreeNode swing =
                new DefaultMutableTreeNode(node);

        for (TreeNode child : node.getChildren()) {
            swing.add(toSwingNode(child));
        }

        return swing;
    }
}