package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

class UserServiceTest {

    @Test
    void registerUser() {
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