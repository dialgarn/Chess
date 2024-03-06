package dataAccess;

import chess.ChessGame;
import model.GameData;
import dataAccess.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String playerName) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() {
        return null;
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

    @Override
    public int getSize() {
        return 0;
    }
}
