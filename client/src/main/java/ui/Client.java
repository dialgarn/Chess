package ui;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import server.Server;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private boolean logged_in = false;
    String authToken;
    int responseValue;
    private final Server server;


    public Client() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    public void run(String[] args) {

        Scanner scanner = new Scanner(System.in);


        label:
        while (true) {
            if (!logged_in) {
                System.out.print("\r[LOGGED_OUT] >>> ");
            } else {
                System.out.print("\r[LOGGED_IN] >>> ");
            }
            String input = scanner.nextLine();
            String[] tokens = input.split("\\s+");

            if (tokens.length > 0) {
                String command = tokens[0];
                switch (command) {
                    case "help":
                        printHelp();
                        break;
                    case "login":
                        if (tokens.length != 3) {
                            System.out.println("Invalid number of arguments. Usage: register <USERNAME> <PASSWORD>");
                        } else {
                            String username = tokens[1];
                            String password = tokens[2];
                            login(username, password);
                        }
                        break;
                    case "register":
                        if (tokens.length != 4) {
                            System.out.println("Invalid number of arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                        } else {
                            String username = tokens[1];
                            String password = tokens[2];
                            String email = tokens[3];
                            register(username, password, email);
                        }
                        break;
                    case "logout":
                        logout();
                        break;
                    case "quit":
                        break label;
                }
            }

        }

        server.stop();
    }


    private void printHelp() {
        if (!logged_in) {
            System.out.println("   register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("   login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("   quit - playing chess");
            System.out.println("   help - with possible commands");
        } else {
            System.out.println("   create <NAME> - a game");
            System.out.println("   list - games");
            System.out.println("   join <ID> [WHITE|BLACK|<empty>] - a game");
            System.out.println("   observe <ID> - a game");
            System.out.println("   logout - when you are done");
            System.out.println("   quit - playing chess");
            System.out.println("   help - with possible commands");
        }
    }

    private void register(String username, String password, String email) {
        String url = "http://localhost:" + server.port() + "/user";
        String requestBody = new Gson().toJson(Map.of(
                "username", username,
                "password", password,
                "email", email
        ));

        try {
            authToken = makeHttpUserPostRequest(requestBody, url);
            logged_in = true;
            System.out.println("Successfully registered and logged in.");
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    private void login(String username, String password) {
        String url = "http://localhost:" + server.port() + "/session";
        String requestBody = new Gson().toJson(Map.of(
                "username", username,
                "password", password
        ));

        try {
            authToken = makeHttpUserPostRequest(requestBody, url);
            if (responseValue == 200) {
                logged_in = true;
                System.out.println("Successfully logged in.");
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

    }

    private void logout() {
        String url = "http://localhost:" + server.port() + "/session";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                responseValue = response.statusCode();
                logged_in = false;
                System.out.println("Successfully logged out.");
            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }
    private String makeHttpUserPostRequest(String requestBody, String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject responseBodyJson = new Gson().fromJson(response.body(), JsonObject.class);
            if (response.statusCode() == 200) {
                responseValue = response.statusCode();
                return responseBodyJson.get("authToken").getAsString();

            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new Exception(e.getMessage());
        }
    }
}
