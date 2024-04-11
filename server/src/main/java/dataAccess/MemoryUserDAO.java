package dataAccess;

import model.UserData;
import Exception.DataAccessException;

import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> userList = new HashMap<>();

    public UserData registerUser(UserData user) throws DataAccessException {
        if (user.username() == null || user.email() == null || user.password() == null) {
            throw new DataAccessException("Bad Request");
        }
        if (getUser(user) == null) {
            userList.put(user.username(), user);
            return user;
        } else {
            throw new DataAccessException("Already Taken");
        }
    }

    public UserData getUser(UserData user) {
        // checks to see if the passed in user (username) already exists. if it does, returns the user information
        for (HashMap.Entry<String, UserData> entry : userList.entrySet()) {
            if (Objects.equals(entry.getValue().username(), user.username()) && Objects.equals(entry.getValue().password(), user.password())) {
                return user;
            }
        }
        return null;
    }

    public UserData login(UserData user) throws DataAccessException {
        if (getUser(user) == null) {
            throw new DataAccessException("Unauthorized");
        } else {
            return user;
        }
    }

    public void clear(){
        userList.clear();
    }

    public int getSize(){
        return userList.size();
    }
}
