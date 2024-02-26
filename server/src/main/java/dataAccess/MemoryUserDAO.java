package dataAccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> userList = new HashMap<>();

    public UserData registerUser(UserData user) throws DataAccessException {

        if (getUser(user) == null) {
            userList.put(user.username(), user);
            return user;
        } else {
            throw new DataAccessException("Bad Request");
        }
    }

    public UserData getUser(UserData user) throws DataAccessException {
        // checks to see if the passed in user (username) already exists. if it does, returns the user information
        for (HashMap.Entry<String, UserData> entry : userList.entrySet()) {
            if (Objects.equals(entry.getValue().username(), user.username())) {
                return user;
            }
        }
        return null;
    }

    public void clear(){
        userList.clear();
    }

    public int getSize(){
        return userList.size();
    }
}
