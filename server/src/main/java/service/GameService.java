package service;

import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName) {
        return this.dataAccess.createGame(gameName);
    }

    public void joinGame() {
        this.dataAccess.joinGame();
    }

    public Collection<GameData> listGames() {
        return this.dataAccess.listGames();
    }
}
