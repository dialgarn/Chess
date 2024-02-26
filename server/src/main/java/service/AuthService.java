package service;

import dataAccess.AuthDAO;
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
}
