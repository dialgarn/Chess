package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData registerUser(UserData user) throws DataAccessException;
    UserData getUser(UserData user) throws DataAccessException;

    void clear();

    int getSize();
}
