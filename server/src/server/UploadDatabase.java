/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Server connection to Azure SQL database
 */
public class UploadDatabase {
    public Connection databaseLink;

    public Connection getConnection() {
        String connectionURL =
                "jdbc:sqlserver://asteria-database.database.windows.net:1433;"
                        + "database=Asteria Database;"
                        + "user=drew@asteria-database;"
                        + "password=Asteria2021;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            databaseLink = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return databaseLink;
    }
}
