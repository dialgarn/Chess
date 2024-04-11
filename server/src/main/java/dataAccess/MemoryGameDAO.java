package dataAccess;

import chess.ChessGame;
import model.GameData;
import Exception.DataAccessException;

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
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String playerName) throws DataAccessException {
        for (GameData game : gameList) {
            if (game.gameID() == gameID) {
                if (playerColor == ChessGame.TeamColor.WHITE) {
                    if (game.whiteUsername() != null) {
                        throw new DataAccessException("Already Taken");
                    }
                    GameData newGame = new GameData(gameID, playerName, game.blackUsername(), game.gameName(), game.game());
                    gameList.remove(game);
                    gameList.add(newGame);
                    return;
                } else if (playerColor == ChessGame.TeamColor.BLACK) {
                    if (game.blackUsername() != null) {
                        throw new DataAccessException("Already Taken");
                    }
                    GameData newGame = new GameData(gameID, game.whiteUsername(), playerName, game.gameName(), game.game());
                    gameList.remove(game);
                    gameList.add(newGame);
                    return;
                } else {
                    return;
                }
            }
        }
        throw new DataAccessException("Bad Request");
    }

    public Collection<GameData> listGames() {
        return gameList;
    }

    public void clear() {
        gameList.clear();
    }

    public int getSize() {
        return gameList.size();
    }
}
