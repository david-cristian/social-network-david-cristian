package org.example.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.example.config.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        Properties props = Config.getProperties();
        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        if (url == null || username == null || password == null)
            throw new RuntimeException("Error: couldn't load properties");

        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
