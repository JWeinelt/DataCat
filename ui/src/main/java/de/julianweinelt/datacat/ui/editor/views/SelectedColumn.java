package de.julianweinelt.datacat.ui.editor.views;

public class SelectedColumn {

    public final String tableAlias;
    public final String tableName;
    public final String columnName;

    public boolean output = true;
    public String alias = "";
    public String sort = "";
    public String filter = "";

    public SelectedColumn(String tableAlias, String tableName, String columnName) {
        this.tableAlias = tableAlias;
        this.tableName = tableName;
        this.columnName = columnName;
    }
}
