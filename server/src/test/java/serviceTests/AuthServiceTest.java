package serviceTests;


import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.AuthService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void createAuthSuccess() {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        AuthData auth = myObject.createAuth(user1);

        Assertions.assertTrue(authDAO.contains(auth));
    }

    @Test
    void createAuthFail() {
        var myObject = new AuthService(new MemoryAuthDAO());
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        AuthData auth = myObject.createAuth(user1);

        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        AuthData auth = myObject.createAuth(user1);

        myObject.deleteAuth(auth.authToken());

        Assertions.assertFalse(authDAO.contains(auth));
    }

    @Test
    void deleteAuthFailure() {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        myObject.createAuth(user1);
        AuthData notRegistered = new AuthData(UUID.randomUUID().toString(), "unregisterUser");

        Assertions.assertThrows(DataAccessException.class, ()->myObject.deleteAuth(notRegistered.authToken()));
    }

    @Test
    void verifySuccess() throws DataAccessException {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        AuthData auth = myObject.createAuth(user1);
        AuthData verifiedOutput = myObject.verify(auth.authToken());

        Assertions.assertEquals(auth, verifiedOutput);
    }

    @Test
    void verifyFailure() {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");

        myObject.createAuth(user1);
        AuthData notRegistered = new AuthData(UUID.randomUUID().toString(), "unregisterUser");

        Assertions.assertThrows(DataAccessException.class, ()->myObject.verify(notRegistered.authToken()));
    }

    @Test
    void clear() {
        var authDAO = new MemoryAuthDAO();
        var myObject = new AuthService(authDAO);

        myObject.createAuth(new UserData("testUser", "123", "test@test.com"));
        myObject.clear();

        int size = authDAO.getSize();
        int expectedSize = 0;

        Assertions.assertEquals(expectedSize, size);
    }
}