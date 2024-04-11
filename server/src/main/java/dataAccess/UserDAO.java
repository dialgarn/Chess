package dataAccess;

import model.UserData;
import Exception.DataAccessException;
public interface UserDAO {
    UserData registerUser(UserData user) throws DataAccessException;
    UserData login(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
}
