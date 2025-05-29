package com.crimemanagement.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaChecker {
    public static void main(String[] args) {
        checkTableSchema("complaints");
    }
    
    public static void checkTableSchema(String tableName) {
        Connection con = null;
        try {
            con = com.crimemanagement.util.DBConnection.getConnection();
            if (con == null) {
                System.err.println("Failed to get database connection for schema check");
                return;
            }
            
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            System.out.println(tableName + " table columns:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("Column: " + columnName + ", Type: " + dataType);
            }
            
            columns.close();
        } catch (SQLException e) {
            System.err.println("Error checking table schema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}