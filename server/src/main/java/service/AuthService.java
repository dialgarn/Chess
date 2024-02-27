package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class AuthService {
    private final AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData createAuth(UserData user) {
        return this.dataAccess.createAuth(user);
    }

    public AuthData getAuth(UserData user) {
        return this.dataAccess.getAuth(user.username());
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        this.dataAccess.deleteAuth(authToken);
    }

    public void verify(String authToken) throws DataAccessException {
        this.dataAccess.verify(authToken);
    }
}
