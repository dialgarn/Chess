package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData registerUser(UserData user) throws DataAccessException;
    UserData login(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
}
