package dataAccess;

import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public UserData registerUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(UserData user) {
        return null;
    }

    @Override
    public UserData login(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }
}
