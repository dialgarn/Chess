package dataAccess;

import chess.ChessGame;
import model.GameData;

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
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
