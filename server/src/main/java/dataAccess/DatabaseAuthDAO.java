package dataAccess;

import model.AuthData;
import model.UserData;
import Exception.DataAccessException;

import java.sql.*;
import java.util.UUID;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(UserData user) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        try (Connection c = DatabaseManager.getConnection()) {
            String selectUserId = "SELECT user_id FROM users WHERE username = ?";

            // First, retrieve the user_id from the users table
            try (PreparedStatement selectStmt = c.prepareStatement(selectUserId)) {
                selectStmt.setString(1, user.username());
                ResultSet rs = selectStmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("User not found.");
                }
                int userId = rs.getInt("user_id");

                // Then, insert the user_id and auth values into the auth table
                String insertAuth = "INSERT INTO auth(user_id, auth) VALUES(?, ?)";
                try (PreparedStatement stmt = c.prepareStatement(insertAuth)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, authToken);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        return authData;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String selectAuth = "SELECT * FROM auth WHERE auth = ?";
            try (PreparedStatement stmt = c.prepareStatement(selectAuth)) {
                stmt.setString(1, authToken);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Unauthorized");
                }

                String deleteAuth = "DELETE FROM auth WHERE auth = ?";
                try (PreparedStatement stmtDelete = c.prepareStatement(deleteAuth)) {
                    stmtDelete.setString(1, authToken);
                    stmtDelete.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unauthorized");
        }
    }

    @Override
    public AuthData verify(String authToken) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String selectAuth = "SELECT users.username, auth FROM auth JOIN users ON auth.user_id = users.user_id " +
                                "WHERE auth.auth = ?";
            try (PreparedStatement stmt = c.prepareStatement(selectAuth)) {
                stmt.setString(1, authToken);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Unauthorized");
                }
                return new AuthData(rs.getString("auth"), rs.getString("username"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unauthorized");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String deleteAll = "DELETE FROM auth";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(deleteAll);
            }
            String resetAutoIncrement = "ALTER TABLE auth AUTO_INCREMENT = 1";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(resetAutoIncrement);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
