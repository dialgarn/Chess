package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Server {

    public final UserService userService = new UserService(new DatabaseUserDAO());
    public final AuthService authService = new AuthService(new DatabaseAuthDAO());
    public final GameService gameService = new GameService(new DatabaseGameDAO());

    public Server() {}

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        // Spark.webSocket("/connect", WebSocketServerConnection.class);
        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.setupDatabase();
        } catch (DataAccessException | SQLException e) {
            return 500;
        }

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::join);
        Spark.get("/game", this::listGames);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object listGames(Request request, Response response) {
        try {
            var authToken = request.headers("Authorization");
            authService.verify(authToken);

            ArrayList<GameData> games = (ArrayList<GameData>) gameService.listGames();

            response.status(200);
            return new Gson().toJson(Map.of("games", games));
        } catch (Throwable e) {
            return errorHandling(request, response, e);
        }
    }

    private Object join(Request request, Response response) {
        try {
            var authToken = request.headers("Authorization");
            AuthData user = authService.verify(authToken);
            var requestBodyJson = new Gson().fromJson(request.body(), JsonObject.class);
            ChessGame.TeamColor playerColor = null;
            if (requestBodyJson.get("playerColor") != null) {
                playerColor = ChessGame.TeamColor.valueOf(requestBodyJson.get("playerColor").getAsString());
            }
            int gameID = requestBodyJson.get("gameID").getAsInt();
            gameService.joinGame(gameID, playerColor, user.username());
            response.status(200);
            return "{}";
        } catch (Throwable e) {
            return errorHandling(request, response, e);
        }
    }

    private Object createGame(Request request, Response response) {
        try {
            var authToken = request.headers("Authorization");
            authService.verify(authToken);
            var requestBodyJson = new Gson().fromJson(request.body(), JsonObject.class);
            String gameName = null;
            if (requestBodyJson.get("gameName") != null) {
                gameName = requestBodyJson.get("gameName").getAsString();
            }
            int gameID = gameService.createGame(gameName);
            response.status(200);
            return String.format("{\"gameID\": \"%d\" }", gameID);
        } catch (Throwable e) {
            return errorHandling(request, response, e);
        }
    }

    private Object logout(Request request, Response response) {
        try {
            var authToken = request.headers("Authorization");
            authService.deleteAuth(authToken);
            response.status(200);
            return "{}";
        } catch (Throwable e) {
            return errorHandling(request, response, e);
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
            return errorHandling(request, response, e);
        }
    }

    public Object clear(Request request, Response response) throws DataAccessException {
        gameService.clear();
        authService.clear();
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
            return errorHandling(request, response, e);
        }
    }

    private Object errorHandling(Request request, Response response, Throwable e) {
        if (e.getMessage() != null) { // Check if the message is not null
            String errorMessage = e.getMessage();
            if (errorMessage.equals("Bad Request")) {
                response.status(400);
                return String.format("{\"message\": \"Error: %s\" }", errorMessage);
            } else if (errorMessage.equals("Unauthorized")) {
                response.status(401);
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

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
