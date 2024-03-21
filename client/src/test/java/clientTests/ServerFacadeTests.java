package clientTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.*;
import ui.Client;


public class ServerFacadeTests {

    private static Client client;
    private static String userUrl;
    private static String loginUrl;
    private static String gameUrl;

    @BeforeAll
    public static void init() throws DataAccessException {
        client = new Client();
        client.clear();


        userUrl = "http://localhost:" + client.server.port() + "/user";
        loginUrl = "http://localhost:" + client.server.port() + "/session";
        gameUrl = "http://localhost:" + client.server.port() + "/game";

    }
    @BeforeEach
    public void setup() throws DataAccessException {
        client.userRequests.register("test",
                "test", "test@email.com", userUrl);
        client.logout();
    }

    @AfterEach
    public void resetDatabase() throws DataAccessException {
        client.clear();
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        client.clear();
        client.server.stop();
    }


    @Test
    public void testRegisterUserSuccess() {
        Assertions.assertDoesNotThrow(()->client.userRequests.register("player1", "password",
                "p1@email.com", userUrl));
    }

    @Test
    public void testRegisterUserFailure() {
        Assertions.assertThrows(DataAccessException.class,()->client.userRequests.register("test",
                "test", "test@email.com", userUrl));
    }

    @Test
    public void testLoginSuccess() {
        Assertions.assertDoesNotThrow(()->client.userRequests.login("test",
                "test", loginUrl));
    }

    @Test
    public void testLoginFailure() {
        Assertions.assertThrows(DataAccessException.class, ()->client.userRequests.login("test",
                "wrongPassword", loginUrl));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        client.userRequests.login("test", "test", loginUrl);
        Assertions.assertDoesNotThrow(()->client.logout());
    }
    @Test
    public void logoutFailure() throws DataAccessException {
        client.userRequests.login("test", "test", loginUrl);
        Assertions.assertThrows(DataAccessException.class, ()->client.userRequests.logout("fakeAuth", loginUrl));
    }

    @Test
    public void createSuccess() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        Assertions.assertDoesNotThrow(()->client.gameRequests.createGame("test", authToken, gameUrl));
    }

    @Test
    public void createFailure() {
        Assertions.assertThrows(DataAccessException.class, ()->client.gameRequests.createGame("test",
                "badAuth", gameUrl));
    }

    @Test
    public void joinSuccess() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        client.gameRequests.createGame("test", authToken, gameUrl);
        Assertions.assertDoesNotThrow(()->client.gameRequests.joinGame(authToken, 1, "WHITE", gameUrl));
    }

    @Test
    public void joinFailure() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        client.gameRequests.createGame("test", authToken, gameUrl);
        Assertions.assertThrows(DataAccessException.class, ()->client.gameRequests.joinGame("badAuth", 1, "WHITE", gameUrl));
    }

    @Test
    public void listSuccess() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        Assertions.assertDoesNotThrow(()->client.gameRequests.getGames(authToken, gameUrl));
    }

    @Test
    public void listFailure() throws DataAccessException {
        client.userRequests.login("test", "test", loginUrl);
        Assertions.assertThrows(DataAccessException.class, ()->client.gameRequests.getGames("badAuth", gameUrl));
    }

    @Test
    public void observeSuccess() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        client.gameRequests.createGame("test", authToken, gameUrl);
        Assertions.assertDoesNotThrow(()->client.gameRequests.joinGame(authToken, 1, null, gameUrl));
    }

    @Test
    public void observeFailure() throws DataAccessException {
        String authToken = client.userRequests.login("test", "test", loginUrl);
        client.gameRequests.createGame("test", authToken, gameUrl);
        Assertions.assertThrows(DataAccessException.class ,()->client.gameRequests.joinGame("badAuth", 1, null, gameUrl));
    }

    @Test
    public void clear() {
        Assertions.assertDoesNotThrow(()->client.clear());
    }

}
