package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService = new UserService(new MemoryUserDAO());
    private final AuthService authService = new AuthService(new MemoryAuthDAO());
    private final GameService gameService = new GameService(new MemoryGameDAO());

    public Server() {}

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        // get
        // post
        // put
        // delete

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        userService.clear();
        response.status(200);
        return "";
    }

    /**
     * Registers a new user if one does not already exist with the given username
     * @param request .
     * @param response .
     * @return authData (username and authToken
     */
    private Object register(Request request, Response response) throws DataAccessException {
        try {
            var user = new Gson().fromJson(request.body(), UserData.class);
            user = userService.registerUser(user);
            AuthData auth = authService.createAuth(user);
            // webSocketHandler.makeNoise(pet.name(), pet.sound());
            return new Gson().toJson(auth);
        } catch (Throwable e) {
            if (e.getMessage().equals("Bad Request")) {
                response.status(400);
            }
            return response;
        }
    }

//    private Object clear(Request request, Response response) {
//    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
