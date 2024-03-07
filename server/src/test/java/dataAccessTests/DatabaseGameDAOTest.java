package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import dataAccess.DatabaseUserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void createGameFailure() {

    }

    @Test
    void joinGameSuccess() {
    }

    @Test
    void joinGameFailure() {

    }

    @Test
    void listGamesSuccess() {
    }

    @Test
    void listGameFailure() {
        
    }

    @Test
    void clear() {
    }
}