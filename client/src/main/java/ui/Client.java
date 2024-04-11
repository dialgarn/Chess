package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.websocket.WebSocketFacade;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private boolean logged_in = false;
    private boolean in_game = false;
    private ChessGame.TeamColor playerColor;
    String authToken = "";
    int currentGameID = 0;
    public final UserRequests userRequests = new UserRequests();

    public final GameRequests gameRequests = new GameRequests();
    private final String sessionUrl;
    private final String userUrl;
    private final String gameUrl;
    private WebSocketFacade ws;
    private final String serverURL;

    private final Map<String, Integer> letters = Map.of(
            "a" , 1,
            "b", 2,
            "c", 3,
            "d", 4,
            "e", 5,
            "f", 6,
            "g", 7,
            "h", 8
        );

    private static final Map<String, ChessPiece.PieceType> pieceTypeMap = Map.of(
            "king", ChessPiece.PieceType.KING,
            "queen", ChessPiece.PieceType.QUEEN,
            "bishop", ChessPiece.PieceType.BISHOP,
            "knight", ChessPiece.PieceType.KNIGHT,
            "rook", ChessPiece.PieceType.ROOK,
            "pawn", ChessPiece.PieceType.PAWN
    );

    public Client() {
        System.out.println("Started test HTTP server on 8080");
        sessionUrl = "http://localhost:8080/session";
        userUrl = "http://localhost:8080/user";
        gameUrl = "http://localhost:8080/game";
        serverURL = "http://localhost:8080";
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
                            in_game = true;
                            playerColor = ChessGame.TeamColor.valueOf(teamColor);
                            ws.setPlayerColor(playerColor);
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "observe":
                        gameID = Integer.parseInt(tokens[1]);
                        observe(gameID);
                        in_game = true;
                        currentGameID = gameID;
                        playerColor = ChessGame.TeamColor.WHITE;
                        break;
                    case "leave":
                        try {
                            ws = new WebSocketFacade(serverURL);
                            ws.leave(authToken, currentGameID);
                            in_game = false;
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "resign":
                        try {
                            ws = new WebSocketFacade(serverURL);
                            ws.resign(authToken, currentGameID);

                            ws.setMessageReceivedCallback(message -> {
                                if (message instanceof NotificationMessage notificationMessage) {
                                    System.out.println(notificationMessage.getMessage());
                                }
                                // You might want to reset the callback here or set a flag that the message has been received
                            });

                            in_game = false;
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "redraw":
                        try {
                            var game = gameRequests.getGame(authToken, currentGameID, gameUrl);
                            if (playerColor == ChessGame.TeamColor.WHITE) {
                                System.out.println(game.game().getBoard().realToStringWhite());
                            } else {
                                System.out.println(game.game().getBoard().realToStringBlack());
                            }
                        } catch (Throwable e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "move":
                        String start, end, promotionString;
                        if (tokens.length == 4) {
                            start = tokens[1];
                            end = tokens[2];
                            promotionString = tokens[3];
                        } else if (tokens.length == 3) {
                            start = tokens[1];
                            end = tokens[2];
                            promotionString = null;
                        } else {
                            System.out.println("Invalid number of arguments. Usage: move <START> <END> [<PROMOTION_PIECE>|<empty>]");
                            break;
                        }

                        try {
                            int rowNumber = Integer.parseInt(start.substring(1));

                            // Map the column letter to a column number
                            String columnLetter = String.valueOf(start.charAt(0));
                            Integer columnNumber = letters.get(columnLetter.toLowerCase());
                            ChessPosition startPosition = new ChessPosition(rowNumber, columnNumber);

                            rowNumber = Integer.parseInt(end.substring(1));

                            // Map the column letter to a column number
                            columnLetter = String.valueOf(end.charAt(0));
                            columnNumber = letters.get(columnLetter.toLowerCase());
                            ChessPosition endPosition = new ChessPosition(rowNumber, columnNumber);

                            ChessPiece.PieceType promotion = null;
                            if (promotionString != null) {
                                promotion = pieceTypeMap.get(promotionString.toLowerCase());
                            }

                            ChessMove move = new ChessMove(startPosition, endPosition, promotion);
                            ws = new WebSocketFacade(serverURL);
                            ws.move(authToken, move, currentGameID);
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
            System.out.println("   move <START> <END> [<PROMOTION_PIECE>|<empty>]- make a move");
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
}
