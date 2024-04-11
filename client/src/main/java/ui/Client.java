package ui;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import server.webSocket.WebSocketHandler;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.LoadGameMessage;

import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private boolean logged_in = false;
    private boolean in_game = false;
    private boolean able_to_move = false;
    String authToken = "";
    int currentGameID = 0;
    public final Server server;
    public final UserRequests userRequests = new UserRequests();

    public final GameRequests gameRequests = new GameRequests();
    private final String sessionUrl;
    private final String userUrl;
    private final String gameUrl;
    private WebSocketFacade ws;
    private final String serverURL;


    public Client() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        sessionUrl = "http://localhost:" + server.port() + "/session";
        userUrl = "http://localhost:" + server.port() + "/user";
        gameUrl = "http://localhost:" + server.port() + "/game";
        serverURL = "http://localhost:" + server.port();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);


        label:
        while (true) {
            if (in_game) {
                System.out.print("\r[IN_GAME} >>> ");
            } else {
                if (!logged_in) {
                    System.out.print("\r[LOGGED_OUT] >>> ");
                } else {
                    System.out.print("\r[LOGGED_IN] >>> ");
                }
            }
            String input = scanner.nextLine();
            String[] tokens = input.split("\\s+");

            if (tokens.length > 0) {
                String command = tokens[0];
                int gameID;
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
                            if (!games.isEmpty()) {
                                for (var game : games) {
                                    System.out.println(game);
                                }
                            } else {
                                System.out.println("There are no games to list.");
                            }
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "join":
                        if (tokens.length == 2) {
                            gameID = Integer.parseInt(tokens[1]);
                            observe(gameID);
                            in_game = true;
                            currentGameID = gameID;
                            break;
                        }
                        gameID = Integer.parseInt(tokens[1]);
                        String teamColor = tokens[2];
                        teamColor = teamColor.toUpperCase();
                        try {
                            gameRequests.joinGame(authToken, gameID, teamColor, gameUrl);
                            ws = new WebSocketFacade(serverURL);

                            ws.setMessageReceivedCallback(message -> {
                                if (message instanceof LoadGameMessage loadGameMessage) {
                                    GameData game = loadGameMessage.getGame();
                                    var color = loadGameMessage.getTeamColor();
                                    if (color == ChessGame.TeamColor.WHITE) {
                                        System.out.println(game.game().getBoard().realToStringWhite());
                                    } else {
                                        System.out.println(game.game().getBoard().realToStringBlack());
                                    }
                                }
                                // You might want to reset the callback here or set a flag that the message has been received
                            });
                            currentGameID = gameID;
                            ws.joinPlayer(authToken, ChessGame.TeamColor.valueOf(teamColor), gameID);
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        in_game = true;
                        able_to_move = true;
                        break;
                    case "observe":
                        gameID = Integer.parseInt(tokens[1]);
                        observe(gameID);
                        in_game = true;
                        currentGameID = gameID;
                        break;
                    case "leave":
                        try {
                            ws = new WebSocketFacade(serverURL);
                            ws.leave(authToken, currentGameID);
                            able_to_move = false;
                            in_game = false;
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

    private void observe(int gameID) {
        try {
            gameRequests.joinGame(authToken, gameID, null, gameUrl);
            ws = new WebSocketFacade(serverURL);

            ws.setMessageReceivedCallback(message -> {
                if (message instanceof LoadGameMessage loadGameMessage) {
                    GameData game = loadGameMessage.getGame();
                    System.out.println(game.game().getBoard().realToStringWhite());
                }
            });

            ws.joinObserver(authToken, gameID);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }


    private void printHelp() {
        if (in_game) {
            System.out.println("   redraw - the chessboard");
            System.out.println("   move - make a move");
            System.out.println("   highlight - legal moves");
            System.out.println("   leave - the match");
            System.out.println("   resign - forfeit the match");
            System.out.println("   help - with possible commands");
        } else {
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
