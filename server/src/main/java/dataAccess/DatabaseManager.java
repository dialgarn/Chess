package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to laod db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void setupDatabase() throws DataAccessException, SQLException {
        try (Connection c = getConnection()) {
            createUsersTable(c);
            createAuthTable(c);
            createGamesTable(c);
        } catch (SQLException e) {
            throw new DataAccessException("Error setting up the database: " + e.getMessage());
        }
    }

    private static void createUsersTable(Connection connection) throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS users(" +
                "user_id integer not null primary key auto_increment," +
                "username VARCHAR(255) not null unique," +
                "password VARCHAR(255) not null," +
                "email VARCHAR(255) not null);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(userTable);
        } catch (SQLException e) {
            throw new SQLException("Error creating the users table: " + e.getMessage(), e);
        }
    }

    private static void createAuthTable(Connection connection) throws SQLException {
        String authTable = "CREATE TABLE IF NOT EXISTS auth(" +
                "auth_id integer not null primary key auto_increment," +
                "user_id integer not null," +
                "auth VARCHAR(255) not null," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(authTable);
        } catch (SQLException e) {
            throw new SQLException("Error creating the auth table: " + e.getMessage(), e);
        }
    }

    private static void createGamesTable(Connection connection) throws SQLException {
        String gamesTable = "CREATE TABLE IF NOT EXISTS games(" +
                "game_id integer not null primary key auto_increment," +
                "white_user_id integer," +
                "black_user_id integer," +
                "game_name VARCHAR(255) not null," +
                "game_data TEXT," +
                "FOREIGN KEY (white_user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (black_user_id) REFERENCES users(user_id));";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(gamesTable);
        } catch (SQLException e) {
            throw new SQLException("Error creating the games table: " + e.getMessage(), e);
        }
    }
}
