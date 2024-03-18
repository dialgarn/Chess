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
            var gameData = new Gson().toJson(new ChessGame());
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
