package serviceTests;

import dataAccess.UserMemoryDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    void registerUser() {
    }

    @Test
    void clear() {
        var myObject = new UserService(new UserMemoryDAO());

        myObject.registerUser(new UserData("testUser", "123", "test@test.com"));
        myObject.clear();

        int size = myObject.getSize();
        int expectedSize = 0;

        Assertions.assertEquals(expectedSize, size);

    }
}