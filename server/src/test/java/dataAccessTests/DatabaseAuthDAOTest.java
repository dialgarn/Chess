package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import dataAccess.DatabaseUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

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
    void createAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void verify() {
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