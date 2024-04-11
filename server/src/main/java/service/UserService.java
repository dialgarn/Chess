package service;

import Exception.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {

    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData registerUser(UserData user) throws DataAccessException {
        return dataAccess.registerUser(user);
    }

    public UserData login(UserData user) throws DataAccessException {
        return dataAccess.login(user);
    }

    public void clear() throws DataAccessException {
        this.dataAccess.clear();
    }

}
