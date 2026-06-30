package de.julianweinelt.datacat.dbx.database;

import java.util.Map;

/**
 * Defines meta information for database types, such as JDBC urls, the syntax and how parameters are processed.
 * @author Julian Weinelt
 * @version 1.0.0
 */
public interface DatabaseMetaData {
    /**
     * Gets the part of the jdbc url after jdbc:[String]://
     * @return The JDBC String
     */
    String jdbcString();

    /**
     * Gets the JDBC URL as a {@link String} object containing all needed placeholders.
     * @return A full JDBC URL with placeholders
     * @implNote Example:
     * <pre>{@code
     * @Override
     * public String jdbcURL() {
     *     return "jdbc:sqlserver://${server};databaseName=${database};${parameters}";
     * }
     * }</pre>
     */
    String jdbcURL();

    /**
     * Gets how this database's syntax is defined.
     * @return A {@link DatabaseSyntax} object defining the syntax
     */
    DatabaseSyntax syntax();

    /**
     * The engine name of this database, e.g. "mysql"
     * @return The engine's name, often the same as {@link #jdbcString()}
     */
    String engineName();

    /**
     * Define how parameters are added to the JDBC URL.
     * @param parameters A {@link Map} contains Strings (key-value-format) as the parameters.
     * @return A {@link String} which can be added to the URL String.
     * @implNote Example (MariaDB/MySQL):
     * <pre>{@code
     * @Override
     *     public String parameters(Map<String, String> parameters) {
     *         StringBuilder paramURL = new StringBuilder("?");
     *         for (Map.Entry<String, String> entry : parameters.entrySet()) {
     *             paramURL.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
     *         }
     *         return paramURL.substring(0, paramURL.length() - 1);
     *     }
     * }</pre>
     */
    String parameters(Map<String, String> parameters);

    /**
     * Get the default parameters of this database type.
     * @return A {@link de.julianweinelt.datacat.dbx.database.ADatabase.ParameterBuilder} object containing all default parameters.
     */
    ADatabase.ParameterBuilder defaultParameters();

    /**
     * Get the default port of this database
     * @return An integer of the port that is used by default
     */
    int defaultPort();
}
