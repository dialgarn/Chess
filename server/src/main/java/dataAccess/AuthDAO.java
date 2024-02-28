package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData user);


    void deleteAuth(String authToken) throws DataAccessException;

    AuthData verify(String authToken) throws DataAccessException;

    void clear();

    int getSize();

    boolean contains(AuthData auth);
}

