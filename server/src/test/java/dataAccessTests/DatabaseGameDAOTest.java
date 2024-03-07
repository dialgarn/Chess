package dataAccessTests;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import dataAccess.DatabaseUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Map;

class DatabaseGameDAOTest {

    public static DatabaseUserDAO userDao;
    public static DatabaseAuthDAO authDao;
    public static DatabaseGameDAO gameDao;

    @BeforeAll
    public static void initialize() {
        userDao = new DatabaseUserDAO();
        authDao = new DatabaseAuthDAO();
        gameDao = new DatabaseGameDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDao.clear();
        authDao.clear();
        userDao.clear();
    }

    @AfterEach
    public void clearDatabase() throws DataAccessException {
        gameDao.clear();
        authDao.clear();
        userDao.clear();
    }

    @Test
    void createGameSuccess() {
        int gameID = Assertions.assertDoesNotThrow(()->gameDao.createGame("testGame"));

        Assertions.assertEquals(1, gameID);
    }

    @Test
    void createGameFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        AuthData fakeAuth = new AuthData("i am a sneaky man", "newUser");

        Assertions.assertThrows(DataAccessException.class, ()->authDao.verify(fakeAuth.authToken()));

        int gameID = Assertions.assertDoesNotThrow(()->gameDao.createGame("testGame"));

        Assertions.assertEquals(1, gameID);
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        authDao.createAuth(newUser);

        int gameID = gameDao.createGame("testGame");

        Assertions.assertDoesNotThrow(()->gameDao.joinGame(gameID, ChessGame.TeamColor.WHITE, "newUser"));
    }

    @Test
    void joinGameFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        authDao.createAuth(newUser);

        int gameID = gameDao.createGame("testGame");
        int badGameID = gameID + 1;


        Assertions.assertThrows(DataAccessException.class, ()->gameDao.joinGame(badGameID, ChessGame.TeamColor.WHITE, "testUser"));
        Assertions.assertThrows(DataAccessException.class, ()->gameDao.joinGame(gameID, ChessGame.TeamColor.WHITE, "unregisteredUser"));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        authDao.createAuth(newUser);
        int gameID = gameDao.createGame("testGame");
        gameDao.joinGame(gameID, ChessGame.TeamColor.WHITE, "newUser");

        String expectedOutput = String.format("{\"games\":[{\"gameID\":%d,\"whiteUsername\":\"%s\",\"gameName\":\"%s\",\"game\":{}}]}"
                , gameID, "newUser", "testGame");

        String myOutput = new Gson().toJson(Map.of("games", gameDao.listGames()));
        Assertions.assertEquals(expectedOutput, myOutput);
    }

    @Test
    void listGameFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        authDao.createAuth(newUser);
        int gameID = gameDao.createGame("testGame");
        gameDao.joinGame(gameID, ChessGame.TeamColor.WHITE, "newUser");

        AuthData fakeAuth = new AuthData("i am a sneaky man", "newUser");

        Assertions.assertThrows(DataAccessException.class, ()->authDao.deleteAuth(fakeAuth.authToken()));

        String badOutput = String.format("{\"games\":[{\"gameID\":%d,\"whiteUsername\":\"%s\",\"gameName\":\"%s\",\"game\":{}}]}"
                , gameID, null, "testGame");

        String myOutput = new Gson().toJson(Map.of("games", gameDao.listGames()));
        Assertions.assertNotEquals(badOutput, myOutput);
    }

    @Test
    void clear() throws DataAccessException {
        gameDao.createGame("newGame");

        Assertions.assertDoesNotThrow(()->gameDao.clear());
    }
}