package server;

import com.google.gson.Gson;
import dataAccess.UserMemoryDAO;
import model.UserData;
import serviceTests.UserService;
import spark.*;

public class Server {

    private final UserService userService = new UserService(new UserMemoryDAO());

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
        return "successfully cleared the database\n";
    }

    private Object register(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), UserData.class);
        // user = userService.registerUser(user) ;
        // webSocketHandler.makeNoise(pet.name(), pet.sound());
        return new Gson().toJson(user);
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
