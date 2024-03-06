package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;

public class DatabaseUserDAO implements UserDAO {

    @Override
    public UserData registerUser(UserData user) throws DataAccessException {
        if (user.username() == null || user.email() == null || user.password() == null) {
            throw new DataAccessException("Bad Request");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encrypted_password = encoder.encode(user.password());
        try (Connection c = DatabaseManager.getConnection()) {
            String insertUser = "INSERT INTO users(username, password, email) " +
                                        "VALUES(?, ?, ?)";
            try (PreparedStatement stmt = c.prepareStatement(insertUser)) {
                stmt.setString(1, user.username());
                stmt.setString(2, encrypted_password);
                stmt.setString(3, user.email());
                stmt.executeUpdate();
                return user;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Already Taken");
        }
    }



    @Override
    public UserData login(UserData user) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String selectUser = "SELECT * FROM users WHERE username = ?";

            try (PreparedStatement stmt = c.prepareStatement(selectUser)) {
                stmt.setString(1, user.username());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Unauthorized");
                }

                // Compare the provided password with the stored hash in the users table
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String storedPassword = rs.getString("password");
                if (!encoder.matches(user.password(), storedPassword)) {
                    throw new DataAccessException("Unauthorized");
                }

                String username = rs.getString("username");
                String email = rs.getString("email");
                return new UserData(username, storedPassword, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unauthorized");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String deleteAll = "DELETE FROM users";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(deleteAll);
            }
            String resetAutoIncrement = "ALTER TABLE users AUTO_INCREMENT = 1";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(resetAutoIncrement);
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
