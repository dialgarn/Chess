package dataAccess;

import model.AuthData;
import model.UserData;

public interface UserDataAccess {
    AuthData registerUser(UserData user);

    void clear();

    int getSize();
}
