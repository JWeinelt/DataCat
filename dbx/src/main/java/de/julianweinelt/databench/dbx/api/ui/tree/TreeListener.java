package de.julianweinelt.databench.dbx.api.ui.tree;

public interface TreeListener {

    void onNodeSelected(TreeNode node);

    void onNodeDoubleClicked(TreeNode node);
}