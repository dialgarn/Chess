package service;

import dataAccess.DataAccessException;
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

    public void clear(){
        this.dataAccess.clear();
    }

    public int getSize(){
        return this.dataAccess.getSize();
    }
}
