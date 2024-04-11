package dataAccess;

import model.AuthData;
import model.UserData;
import Exception.DataAccessException;

public interface AuthDAO {
    AuthData createAuth(UserData user) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    AuthData verify(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}

