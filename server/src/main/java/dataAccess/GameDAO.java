package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName);
    void joinGame();
    Collection<GameData> listGames();
}
