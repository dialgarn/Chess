package service;

import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class GameService {

    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }
}
