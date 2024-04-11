package dataAccessTests;

import Exception.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import dataAccess.DatabaseUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

class DatabaseUserDAOTest {

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
    void registerUserSuccess() {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");
        Assertions.assertDoesNotThrow(()->userDao.registerUser(newUser));

        UserData user2 = new UserData("newUser", "hello", "myemail@gmail.com");
        Assertions.assertThrows(DataAccessException.class, ()->userDao.registerUser(user2));

    }

    @Test
    void registerUserFail() throws DataAccessException {
        UserData newUser = new UserData("newUser", null, "test@gmail.com");
        Assertions.assertThrows(DataAccessException.class, ()->userDao.registerUser(newUser));
    }

    @Test
    void loginSuccess() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        userDao.registerUser(user1);

        UserData userToLogin = new UserData("user1", "password1", null);

        Assertions.assertDoesNotThrow(()->userDao.login(userToLogin));
    }

    @Test
    void loginFail() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        userDao.registerUser(user1);

        UserData userToLogin = new UserData("user1", "fakePassword", null);

        Assertions.assertThrows(DataAccessException.class, ()->userDao.login(userToLogin));
    }

    @Test
    void clear() throws DataAccessException {
        UserData newUser = new UserData("newUser", "test", "test@gmail.com");
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");
        UserData user2 = new UserData("user2", "hello", "myemail@gmail.com");

        userDao.registerUser(newUser);
        userDao.registerUser(user1);
        userDao.registerUser(user2);

        Assertions.assertDoesNotThrow(()->userDao.clear());
    }
}