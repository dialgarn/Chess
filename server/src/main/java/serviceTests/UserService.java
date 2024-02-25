package serviceTests;

import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDataAccess dataAccess;

    public UserService(UserDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) {
        return dataAccess.registerUser(user);
    }

    public void clear(){
        this.dataAccess.clear();
    }

    public int getSize(){
        return this.dataAccess.getSize();
    }
}
