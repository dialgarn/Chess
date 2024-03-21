package serviceTests;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.GameService;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void createGameSuccess() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        myObject.createGame("testGame");


        int size = gameDAO.getSize();
        int expectedSize = 1;

        Assertions.assertEquals(expectedSize, size);
    }

    @Test
    void createGameFailure() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        myObject.createGame("testGame");

        var authDAO = new MemoryAuthDAO();
        var authService = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        authService.createAuth(user1);
        AuthData notRegistered = new AuthData(UUID.randomUUID().toString(), "unregisterUser");

        Assertions.assertThrows(DataAccessException.class, ()->authService.verify(notRegistered.authToken()));

        int size = gameDAO.getSize();
        int expectedSize = 1;

        Assertions.assertEquals(expectedSize, size);
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        int gameID = myObject.createGame("testGame");


        Assertions.assertDoesNotThrow(()->myObject.joinGame(gameID, ChessGame.TeamColor.WHITE, "testUser"));
    }

    @Test
    void joinGameFailure() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        int gameID = myObject.createGame("testGame");
        int badGameID = gameID + 1;


        Assertions.assertThrows(DataAccessException.class, ()->myObject.joinGame(badGameID, ChessGame.TeamColor.WHITE, "testUser"));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        int gameID = myObject.createGame("testGame");
        myObject.joinGame(gameID, ChessGame.TeamColor.WHITE, "testUser");

        Assertions.assertDoesNotThrow(myObject::listGames);
    }

    @Test
    void listGamesFailure() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        int gameID = myObject.createGame("testGame");
        myObject.joinGame(gameID, ChessGame.TeamColor.WHITE, "testUser");

        String expectedOutput = String.format("{\"games\":[{\"gameID\":%d,\"whiteUsername\":\"%s\",\"gameName\":\"%s\",\"game\":{}}]}"
                , gameID, "testUser", "testGame");

        var authDAO = new MemoryAuthDAO();
        var authService = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        authService.createAuth(user1);
        AuthData notRegistered = new AuthData(UUID.randomUUID().toString(), "unregisterUser");

        Assertions.assertThrows(DataAccessException.class, ()->authService.verify(notRegistered.authToken()));
    }

    @Test
    void clear() throws DataAccessException {
        var gameDAO = new MemoryGameDAO();
        var myObject = new GameService(gameDAO);

        myObject.createGame("testGame");
        myObject.clear();

        int size = gameDAO.getSize();
        int expectedSize = 0;

        Assertions.assertEquals(expectedSize, size);
    }
}