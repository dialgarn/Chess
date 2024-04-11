package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String createNewGame = "INSERT INTO games(game_name, game_data) VALUES(?, ?)";
            // var gameData = new Gson().toJson(new ChessGame());
            var game = new ChessGame();
            game.getBoard().resetBoard();
            var gameData = new Gson().toJson(game);
            try (PreparedStatement stmt = c.prepareStatement(createNewGame)) {
                stmt.setString(1, gameName);
                stmt.setString(2, gameData);
                stmt.executeUpdate();
            }

            String getGameID = "SELECT game_id FROM games game_id WHERE game_name = ?";
            try (PreparedStatement stmt = c.prepareStatement(getGameID)) {
                stmt.setString(1, gameName);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Failed to get game ID");
                }
                return rs.getInt("game_id");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void updateChessGame(int gameID, ChessGame game) throws DataAccessException {
        // Serialize the GameData object into a String (assuming you're using JSON serialization)
        String serializedGameData = new Gson().toJson(game);

        try (Connection c = DatabaseManager.getConnection()) {
            // First, check if the game exists
            String checkGameExists = "SELECT * FROM games WHERE game_id = ?";
            try (PreparedStatement checkStmt = c.prepareStatement(checkGameExists)) {
                checkStmt.setInt(1, gameID);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Game not found");
                }
            }

            // Update the game_data for the specified gameID
            String updateGameData = "UPDATE games SET game_data = ? WHERE game_id = ?";
            try (PreparedStatement updateStmt = c.prepareStatement(updateGameData)) {
                updateStmt.setString(1, serializedGameData); // Set the serialized GameData
                updateStmt.setInt(2, gameID); // Specify which game to update
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    // If no rows were affected, it means the gameID does not exist
                    throw new DataAccessException("Game not found");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error updating game data: " + e.getMessage());
        }
    }
    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String playerName) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {


            String getUsername = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement stmt = c.prepareStatement(getUsername)) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Bad Request");
                }
                int playerID = rs.getInt("user_id");

                String checkGameExists = "SELECT * FROM games WHERE game_id = ?";
                try (PreparedStatement checkStmt = c.prepareStatement(checkGameExists)) {
                    checkStmt.setInt(1, gameID);
                    rs = checkStmt.executeQuery();
                    if (!rs.next()) {
                        throw new DataAccessException("Bad Request");
                    }
                }

                String checkAvailability = "SELECT white_user_id, black_user_id FROM games WHERE game_id = ?";
                try (PreparedStatement checkStmt = c.prepareStatement(checkAvailability)) {
                    checkStmt.setInt(1, gameID);
                    rs = checkStmt.executeQuery();
                    if (!rs.next()) {
                        throw new DataAccessException("Bad Request");
                    }
                    if (rs.getInt("white_user_id") != 0 && playerColor == ChessGame.TeamColor.WHITE) {
                            throw new DataAccessException("Already Taken");
                    }
                    if (rs.getInt("black_user_id") != 0 && playerColor == ChessGame.TeamColor.BLACK) {
                        throw new DataAccessException("Already Taken");
                    }
                }
                if (playerColor == ChessGame.TeamColor.WHITE) {
                    String updateGame = "UPDATE games SET white_user_id = ? WHERE game_id = ?";
                    try (PreparedStatement updateStmt = c.prepareStatement(updateGame)) {
                        updateStmt.setInt(1, playerID);
                        updateStmt.setInt(2, gameID);
                        updateStmt.executeUpdate();
                    }
                } else if (playerColor == ChessGame.TeamColor.BLACK) {
                    String updateGame = "UPDATE games SET black_user_id = ? WHERE game_id = ?";
                    try (PreparedStatement updateStmt = c.prepareStatement(updateGame)) {
                        updateStmt.setInt(1, playerID);
                        updateStmt.setInt(2, gameID);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Bad Request");
        }
    }

    public void playerLeavesGame(int gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            // Define the SQL statement based on the player's color
            String updatePlayer;
            if (playerColor == ChessGame.TeamColor.WHITE) {
                updatePlayer = "UPDATE games SET white_user_id = NULL WHERE game_id = ?";
            } else if (playerColor == ChessGame.TeamColor.BLACK) {
                updatePlayer = "UPDATE games SET black_user_id = NULL WHERE game_id = ?";
            } else {
                throw new IllegalArgumentException("Invalid player color");
            }

            // Execute the update
            try (PreparedStatement updateStmt = c.prepareStatement(updatePlayer)) {
                updateStmt.setInt(1, gameID);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    // If no rows were affected, it means the gameID does not exist
                    throw new DataAccessException("Game not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating player status: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection()) {
            String getGames = "SELECT game_id, white_user_id, black_user_id, game_name, game_data FROM games";
            String getPlayer = "SELECT username FROM users WHERE user_id = ?";

            try (PreparedStatement stmt = c.prepareStatement(getGames)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    int gameID = rs.getInt("game_id");
                    String gameName = rs.getString("game_name");
                    String gameData = rs.getString("game_data");
                    ChessGame game = new Gson().fromJson(gameData, ChessGame.class);
                    String whitePlayerName;
                    String blackPlayerName;
                    try (PreparedStatement getWhitePlayerStmt = c.prepareStatement(getPlayer)) {
                        try {
                            getWhitePlayerStmt.setInt(1, rs.getInt("white_user_id"));
                            ResultSet whiteRS = getWhitePlayerStmt.executeQuery();
                            whiteRS.next();
                            whitePlayerName = whiteRS.getString("username");
                        } catch (SQLException e) {
                            whitePlayerName = null;
                        }
                    }
                    try (PreparedStatement getBlackPlayerStmt = c.prepareStatement(getPlayer)) {
                        try {
                            getBlackPlayerStmt.setInt(1, rs.getInt("black_user_id"));
                            ResultSet blackRS = getBlackPlayerStmt.executeQuery();
                            blackRS.next();
                            blackPlayerName = blackRS.getString("username");
                        } catch (SQLException e) {
                            blackPlayerName = null;
                        }
                    }

                    games.add(new GameData(gameID, whitePlayerName, blackPlayerName, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return games;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection c = DatabaseManager.getConnection()) {
            String deleteAll = "DELETE FROM games";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(deleteAll);
            }
            String resetAutoIncrement = "ALTER TABLE games AUTO_INCREMENT = 1";
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(resetAutoIncrement);
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
