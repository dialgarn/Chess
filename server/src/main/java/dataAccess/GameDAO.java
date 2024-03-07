package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    void joinGame(int gameID, ChessGame.TeamColor playerColor, String playerName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    void clear() throws DataAccessException;

    int getSize();
}
