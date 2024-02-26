package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;

public class GeneralService {
    // private final GameService gameService = new
    private final UserService userService;
    private final AuthService authService;

    GeneralService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        userService = new UserService(userDAO);
        authService = new AuthService(authDAO);
    }
}
