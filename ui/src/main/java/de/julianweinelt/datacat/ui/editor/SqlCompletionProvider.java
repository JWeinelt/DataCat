package de.julianweinelt.datacat.ui.editor;

import de.julianweinelt.datacat.dbx.database.ADatabase;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.text.JTextComponent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlCompletionProvider extends DefaultCompletionProvider {
    private final ADatabase dataSource;

    public SqlCompletionProvider(ADatabase dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Completion> getCompletions(JTextComponent comp) {
        List<Completion> completions = new ArrayList<>();

        String sql = comp.getText();
        int caret = comp.getCaretPosition();

        String context = determineContext(sql, caret);

        switch (context) {
            case "FROM" -> addTableCompletions(completions);
            case "SELECT" -> addColumnCompletions(completions, sql);
            case "WHERE" -> addColumnCompletions(completions, sql);
        }

        return completions;
    }

    private void addTableCompletions(List<Completion> completions) {
        try (Connection conn = dataSource.connection()) {

            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {

                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");

                    completions.add(
                            new BasicCompletion(this, table)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addColumnCompletions(List<Completion> completions, String sql) {

        String table = extractTableName(sql);

        if (table == null) {
            return;
        }

        try (Connection conn = dataSource.connection()) {

            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getColumns(null, null, table, "%")) {

                while (rs.next()) {

                    String column = rs.getString("COLUMN_NAME");

                    completions.add(
                            new BasicCompletion(this, column)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String determineContext(String sql, int caret) {

        String beforeCaret = sql.substring(0, caret).toUpperCase();

        if (beforeCaret.matches("(?s).*\\bFROM\\s+\\w*$")) {
            return "FROM";
        }

        if (beforeCaret.matches("(?s).*\\bWHERE\\s+\\w*$")) {
            return "WHERE";
        }

        if (beforeCaret.matches("(?s).*\\bSELECT\\s+.*")) {
            return "SELECT";
        }

        return "DEFAULT";
    }

    private String extractTableName(String sql) {
        Pattern pattern = Pattern.compile(
                "\\bFROM\\s+([a-zA-Z0-9_]+)",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}