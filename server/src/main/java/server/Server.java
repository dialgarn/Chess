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
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        // get
        // post
        // put
        // delete

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object logout(Request request, Response response) {
        try {
            var authToken = request.headers("Authorization");
            authService.deleteAuth(authToken);
            response.status(200);
            return "{}";
        } catch (Throwable e) {
            if (e.getMessage() != null) { // Check if the message is not null
                String errorMessage = e.getMessage();
                if (errorMessage.equals("Unauthorized")) {
                    response.status(401);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                } else {
                    response.status(500);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                }
            } else {
                response.status(500);
                return "{\"message\": \"Error: Unknown\" }"; // Provide a default error message
            }
        }
    }

    private Object login(Request request, Response response) {
        try {
            var user = new Gson().fromJson(request.body(), UserData.class);
            user = userService.login(user);
            AuthData auth = authService.createAuth(user);
            response.status(200);
            return new Gson().toJson(auth);
        } catch (Throwable e) {
            if (e.getMessage() != null) { // Check if the message is not null
                String errorMessage = e.getMessage();
                if (errorMessage.equals("Unauthorized")) {
                    response.status(401);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                } else {
                    response.status(500);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                }
            } else {
                response.status(500);
                return "{\"message\": \"Error: Unknown\" }"; // Provide a default error message
            }
        }
    }

    private Object clear(Request request, Response response) {
        userService.clear();
        response.status(200);
        return "{}";
    }

    /**
     * Registers a new user if one does not already exist with the given username
     * @param request .
     * @param response .
     * @return authData (username and authToken
     */
    private Object register(Request request, Response response) {
        try {
            var user = new Gson().fromJson(request.body(), UserData.class);
            user = userService.registerUser(user);
            AuthData auth = authService.createAuth(user);
            response.status(200);
            return new Gson().toJson(auth);
        } catch (Throwable e) {
            if (e.getMessage() != null) { // Check if the message is not null
                String errorMessage = e.getMessage();
                if (errorMessage.equals("Bad Request")) {
                    response.status(400);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                } else if (errorMessage.equals("Already Taken")) {
                    response.status(403);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                } else {
                    response.status(500);
                    return String.format("{\"message\": \"Error: %s\" }", errorMessage);
                }
            } else {
                response.status(500);
                return "{\"message\": \"Error: Unknown\" }"; // Provide a default error message
            }
        }
    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
