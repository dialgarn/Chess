package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

class UserServiceTest {

    @Test
    void registerUser() throws DataAccessException {
        var myObject = new  UserService(new MemoryUserDAO());
        UserData user1 = new UserData("user1", "password1", "user1@gmail.com");
        UserData user2 = new UserData("user2", "password2", "user2@gmail.com");

        myObject.registerUser(user1);
        myObject.registerUser(user2);

        int size = myObject.getSize();

        Assertions.assertEquals(2, size);
    }

    @Test
    void clear() throws DataAccessException {
        var myObject = new UserService(new MemoryUserDAO());

        myObject.registerUser(new UserData("testUser", "123", "test@test.com"));
        myObject.clear();

        int size = myObject.getSize();
        int expectedSize = 0;

        Assertions.assertEquals(expectedSize, size);

    }
}