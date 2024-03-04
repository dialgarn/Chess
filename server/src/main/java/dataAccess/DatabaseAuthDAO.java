package dataAccess;

import model.AuthData;
import model.UserData;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(UserData user) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData verify(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean contains(AuthData auth) {
        return false;
    }
}
