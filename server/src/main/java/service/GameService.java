package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName) throws DataAccessException {
        return this.dataAccess.createGame(gameName);
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String playerName) throws DataAccessException {
        this.dataAccess.joinGame(gameID, playerColor, playerName);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return this.dataAccess.listGames();
    }

    public void clear() throws DataAccessException {
        this.dataAccess.clear();
    }
}
