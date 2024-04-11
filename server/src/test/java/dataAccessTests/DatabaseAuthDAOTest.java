package dataAccessTests;

import Exception.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import dataAccess.DatabaseUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

class DatabaseAuthDAOTest {

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
    void createAuthSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        Assertions.assertDoesNotThrow(()->authDao.createAuth(newUser));
    }

    @Test
    void createAuthFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        UserData notRegisteredUser = new UserData("noob", "kekw", "yuh@yahoo.com");

        Assertions.assertThrows(DataAccessException.class, ()->authDao.createAuth(notRegisteredUser));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        AuthData auth = authDao.createAuth(newUser);

        Assertions.assertDoesNotThrow(()->authDao.deleteAuth(auth.authToken()));
    }

    @Test
    void deleteAuthFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        AuthData fakeAuth = new AuthData("i am a sneaky man", "newUser");

        Assertions.assertThrows(DataAccessException.class, ()->authDao.deleteAuth(fakeAuth.authToken()));
    }

    @Test
    void verifySuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        AuthData auth = authDao.createAuth(newUser);

        Assertions.assertDoesNotThrow(()->authDao.verify(auth.authToken()));
    }

    @Test
    void verifyFailure() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");

        userDao.registerUser(newUser);

        AuthData fakeAuth = new AuthData("i am a sneaky man", "newUser");

        Assertions.assertThrows(DataAccessException.class, ()->authDao.verify(fakeAuth.authToken()));
    }

    @Test
    void clear() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");
        UserData user2 = new UserData("user2", "hello", "myemail@gmail.com");

        userDao.registerUser(newUser);
        userDao.registerUser(user1);
        userDao.registerUser(user2);

        authDao.createAuth(newUser);
        authDao.createAuth(user1);
        authDao.createAuth(user2);

        Assertions.assertDoesNotThrow(()->authDao.clear());
    }
}