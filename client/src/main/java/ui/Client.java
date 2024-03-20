package ui;

import model.GameData;
import server.Server;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private boolean logged_in = false;
    String authToken = "";
    private final Server server;
    private final UserRequests userRequests = new UserRequests();

    private final GameRequests gameRequests = new GameRequests();


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
                            String url = "http://localhost:" + server.port() + "/session";
                            String username = tokens[1];
                            String password = tokens[2];
                            try {
                                authToken = userRequests.login(username, password, url);
                                logged_in = true;
                            } catch (Throwable e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        break;
                    case "register":
                        if (tokens.length != 4) {
                            System.out.println("Invalid number of arguments. Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                        } else {
                            String url = "http://localhost:" + server.port() + "/user";
                            String username = tokens[1];
                            String password = tokens[2];
                            String email = tokens[3];
                            try {
                                authToken = userRequests.register(username, password, email, url);
                                logged_in = true;
                            } catch (Throwable e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        break;
                    case "logout":
                        logout();
                        break;
                    case "create":
                        if (tokens.length != 2) {
                            System.out.println("Invalid number of arguments. Usage: create <NAME>");
                        } else {
                            String url = "http://localhost:" + server.port() + "/game";
                            String gameName = tokens[1];
                            try {
                                gameRequests.createGame(gameName, authToken, url);
                            } catch (Throwable e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        break;
                    case "list":
                        String url = "http://localhost:" + server.port() + "/game";
                        try {
                            ArrayList<GameData> games = (ArrayList<GameData>) gameRequests.listGames(authToken, url);
                            for (var game : games) {
                                System.out.println(game);
                            }
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "join":
                        url = "http://localhost:" + server.port() + "/game";
                        int gameID = Integer.parseInt(tokens[1]);
                        String teamColor = tokens[2];
                        try {
                            gameRequests.joinGame(authToken, gameID, teamColor, url);
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "quit":
                        if (logged_in) {
                            logout();
                        }
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

    private void logout() {
        String url = "http://localhost:" + server.port() + "/session";
        try {
            userRequests.logout(url);
            logged_in = false;
            authToken = "";
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

}
