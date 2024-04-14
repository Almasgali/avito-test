package ru.almasgali.avito.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class Util {

    static final String initPath = "src/main/resources/db/init.sql";
    static final String insertPath = "src/main/resources/db/insert.sql";
    static final String jdbcUrl = "jdbc:postgresql://localhost:5656/";


    public static void runScript(String path, Connection connection) {
        boolean continueOrError = false;
        boolean ignoreFailedDrops = false;
        String commentPrefix = "--";
        String separator = ";";
        String blockCommentStartDelimiter = "/*";
        String blockCommentEndDelimiter = "*/";

        ScriptUtils.executeSqlScript(
                connection,
                new EncodedResource(new PathResource(path)),
                continueOrError,
                ignoreFailedDrops,
                commentPrefix,
                separator,
                blockCommentStartDelimiter,
                blockCommentEndDelimiter
        );
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, "postgres", "postgres");
    }

    public static void initDB() throws SQLException {
        try (Connection connection = getConnection()) {
            runScript(initPath, connection);
            runScript(insertPath, connection);
        }
    }
}
