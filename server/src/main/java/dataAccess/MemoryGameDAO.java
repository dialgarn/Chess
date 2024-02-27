package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    private final HashSet<GameData> gameList = new HashSet<>();
    private int gameCounter = 1;

    public int createGame(String gameName) {
        GameData game = new GameData(gameCounter, null, null, gameName, new ChessGame());
        gameList.add(game);
        gameCounter += 1;
        return game.gameID();
    }

    public void joinGame() {

    }

    public Collection<GameData> listGames() {
        return gameList;
    }
}
