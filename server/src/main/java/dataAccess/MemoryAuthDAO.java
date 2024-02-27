package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashSet<AuthData> authList = new HashSet<>();

    public AuthData createAuth(UserData username){
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username.username());
        authList.add(authData);
        return authData;
    }

    public AuthData getAuth(String username) {
        for (AuthData auth : authList){
            if (Objects.equals(auth.username(), username)) {
                return auth;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authList) {
            if (Objects.equals(auth.authToken(), authToken)) {
                authList.remove(auth);
                return;
            }
        }
        throw new DataAccessException("Unauthorized");
    }

    public void verify(String authToken) throws DataAccessException {
        for (AuthData auth : authList) {
            if (Objects.equals(auth.authToken(), authToken)) {
                return;
            }
        }
        throw new DataAccessException("Unauthorized");
    }
}
