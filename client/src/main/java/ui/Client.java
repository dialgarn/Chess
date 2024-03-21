package ui;

import dataAccess.DataAccessException;
import model.GameData;
import server.Server;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private boolean logged_in = false;
    String authToken = "";
    public final Server server;
    public final UserRequests userRequests = new UserRequests();

    public final GameRequests gameRequests = new GameRequests();
    private final String sessionUrl;
    private final String userUrl;
    private final String gameUrl;


    public Client() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        sessionUrl = "http://localhost:" + server.port() + "/session";
        userUrl = "http://localhost:" + server.port() + "/user";
        gameUrl = "http://localhost:" + server.port() + "/game";
    }

    public void run() {

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
                            try {
                                authToken = userRequests.login(username, password, sessionUrl);
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
                            String username = tokens[1];
                            String password = tokens[2];
                            String email = tokens[3];
                            try {
                                authToken = userRequests.register(username, password, email, userUrl);
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
                            String gameName = tokens[1];
                            try {
                                gameRequests.createGame(gameName, authToken, gameUrl);
                            } catch (Throwable e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        break;
                    case "list":
                        try {
                            ArrayList<GameData> games = (ArrayList<GameData>) gameRequests.getGames(authToken, gameUrl);
                            for (var game : games) {
                                System.out.println(game);
                            }
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "join":
                        int gameID = Integer.parseInt(tokens[1]);
                        String teamColor = tokens[2];
                        teamColor = teamColor.toUpperCase();
                        try {
                            gameRequests.joinGame(authToken, gameID, teamColor, gameUrl);
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "observe":
                        gameID = Integer.parseInt(tokens[1]);
                        try {
                            gameRequests.joinGame(authToken, gameID, null, gameUrl);
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

    public void logout() {
        try {
            userRequests.logout(authToken, sessionUrl);
            logged_in = false;
            authToken = "";
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        server.gameService.clear();
        server.authService.clear();
        server.userService.clear();
    }
}
