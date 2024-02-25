package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserMemoryDAO implements UserDataAccess {

    private final HashMap<String, UserData> userList = new HashMap<>();

    public AuthData registerUser(UserData user) {

        if (getUser(user) == null) {
            userList.put(user.getUsername(), user);
            return null;
        } else {
            return null;
        }
    }

    public UserData getUser(UserData user) {
        // checks to see if the passed in user (username) already exists. if it does, returns the user information
        for (HashMap.Entry<String, UserData> entry : userList.entrySet()) {
            if (Objects.equals(entry.getValue().getUsername(), user.getUsername())) {
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
