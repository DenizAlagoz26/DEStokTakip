package com.example.destoktakip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/DEStok_Takip";
    private static final String USER = "root";
    private static final String PASSWORD = "senin_mysql_şifren";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
