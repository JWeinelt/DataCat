package de.julianweinelt.databench.dbx.api.ui.tree;

public enum DefaultNodeTypes implements TreeNodeType {
    PROJECT,
    DATABASES,
    DATABASE,
    TABLES,
    TABLE,
    COLUMNS,
    COLUMN,
    INDICES,
    INDEX,
    VIEWS,
    VIEW,
    JOB_AGENT;

    @Override
    public String getId() {
        return this.name();
    }
}