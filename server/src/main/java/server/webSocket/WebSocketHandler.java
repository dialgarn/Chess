package server.webSocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDAO;
import dataAccess.DatabaseGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private final DatabaseGameDAO gameDao = new DatabaseGameDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // Deserialize the incoming JSON message into a command object
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
            String auth = jsonObject.get("authToken").getAsString();;
            int gameID;
                switch (command.getCommandType()) {
                    case JOIN_PLAYER:
                        // Handle join player command
                        String playerColorValue = jsonObject.get("playerColor").getAsString();
                        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(playerColorValue.toUpperCase());
                        gameID = jsonObject.get("gameID").getAsInt();

                        JoinCommand joinCommand = new JoinCommand(auth, color, gameID);
                        joinPlayer(joinCommand, session);
                        break;
                    case JOIN_OBSERVER:
                        // Handle join observer command
                        gameID = jsonObject.get("gameID").getAsInt();

                        JoinObserver observerCommand = new JoinObserver(auth, gameID);
                        joinObserver(observerCommand, session);
                        break;
                    case MAKE_MOVE:
                        // Handle make move command
                        gameID = jsonObject.get("gameID").getAsInt();
                        JsonObject moveObject = jsonObject.getAsJsonObject("move"); // Get the "move" JSON object
                        ChessMove chessMove = new Gson().fromJson(moveObject, ChessMove.class);
                        JsonObject teamColor = jsonObject.getAsJsonObject("playerColor");
                        ChessGame.TeamColor playerColor = new Gson().fromJson(teamColor, ChessGame.TeamColor.class);
                        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(auth, chessMove, gameID);
                        makeMove(makeMoveCommand, session);
                        break;
                    case LEAVE:
                        // Handle leave command
                        gameID = jsonObject.get("gameID").getAsInt();
                        LeaveCommand leaveCommand = new LeaveCommand(auth, gameID);
                        leave(leaveCommand, session);
                        break;
                    case RESIGN:
                        gameID = jsonObject.get("gameID").getAsInt();
                        ResignCommand resignCommand = new ResignCommand(auth, gameID);
                        resign(resignCommand, session);
                        break;
                }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println(e.getMessage());
        }
    }

    public void leave(LeaveCommand command, Session session) throws DataAccessException, IOException {
        AuthData auth = authDAO.verify(command.getAuthString());
        NotificationMessage notification = new NotificationMessage(String.format("%s left the game", auth.username()));

        GameData game = getGameToPrint(command.getGameID(), session);

        assert game != null;
        if (game.whiteUsername() != null && Objects.equals(auth.username(), game.whiteUsername())) {
            gameDao.playerLeavesGame(game.gameID(), ChessGame.TeamColor.WHITE);
        } else if (game.blackUsername() != null && Objects.equals(auth.username(), game.blackUsername())) {
            gameDao.playerLeavesGame(game.gameID(), ChessGame.TeamColor.BLACK);
        }

        connections.broadcast(command.getAuthString(), new Gson().toJson(notification));
        connections.remove(command.getAuthString());
    }

    public void resign(ResignCommand command, Session session) throws DataAccessException, IOException {
        AuthData auth = authDAO.verify(command.getAuthString());
        NotificationMessage notification = new NotificationMessage(String.format("%s has resigned from the game", auth.username()));
        String jsonMessage = new Gson().toJson(notification);

        GameData game = getGameToPrint(command.getGameID(), session);

        assert game != null;
        if (!Objects.equals(auth.username(), game.blackUsername()) && !Objects.equals(auth.username(), game.whiteUsername())) {
            ErrorMessage error = new ErrorMessage("An observer cannot resign.");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        if (game.game().isGame_over()) {
            ErrorMessage error = new ErrorMessage("Game already over.");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }

        game.game().setGame_over(true);
        gameDao.updateChessGame(game.gameID(), game.game());
        connections.broadcast("", jsonMessage);
        connections.remove(command.getAuthString());
    }

    public void makeMove(MakeMoveCommand command, Session session) throws IOException, DataAccessException, InvalidMoveException {
        AuthData auth = authDAO.verify(command.getAuthString());
        ChessMove move = command.getMove();
        GameData game = getGameToPrint(command.getGameID(), session);
        assert game != null;

        ChessGame.TeamColor color = null;
        if (Objects.equals(game.whiteUsername(), auth.username())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(game.blackUsername(), auth.username())) {
            color = ChessGame.TeamColor.BLACK;
        }


        if (color != game.game().getTeamTurn()) {
            ErrorMessage errorMessage = new ErrorMessage("Not your turn");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        if (color != game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
            ErrorMessage errorMessage = new ErrorMessage("Not your piece");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }


        if (game.game().isGame_over()) {
            ErrorMessage errorMessage = new ErrorMessage("Game Over");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        var validMoves = game.game().validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
                game.game().makeMove(move);
        } else {
            ErrorMessage errorMessage = new ErrorMessage("Invalid move");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }


        boolean whiteCheck = game.game().isInCheck(ChessGame.TeamColor.WHITE);
        boolean blackCheck = game.game().isInCheck(ChessGame.TeamColor.BLACK);

        boolean blackCheckmate = game.game().isInCheckmate(ChessGame.TeamColor.BLACK);
        boolean whiteCheckMate = game.game().isInCheckmate(ChessGame.TeamColor.WHITE);
        if (whiteCheckMate) {
            game.game().setGame_over(true);
            NotificationMessage checkmate = new NotificationMessage(String.format("%s is in checkmate", game.whiteUsername()));
            connections.broadcast("", new Gson().toJson(checkmate));
        } else if (blackCheckmate) {
            game.game().setGame_over(true);
            NotificationMessage checkmate = new NotificationMessage(String.format("%s is in checkmate", game.blackUsername()));
            connections.broadcast("", new Gson().toJson(checkmate));
        } else if (whiteCheck) {
            NotificationMessage check = new NotificationMessage(String.format("%s is in check", game.whiteUsername()));
            connections.broadcast("", new Gson().toJson(check));
        } else if (blackCheck) {
            NotificationMessage check = new NotificationMessage(String.format("%s is in check", game.blackUsername()));
            connections.broadcast("", new Gson().toJson(check));
        }

        gameDao.updateChessGame(game.gameID(), game.game());

        connections.broadcastMove(game);

        NotificationMessage message = new NotificationMessage(String.format("Move made: %s, %s", move.getStartPosition(), move.getEndPosition()));
        String jsonMessage = new Gson().toJson(message);
        connections.broadcast(command.getAuthString(), jsonMessage);
    }

    public void joinObserver(JoinObserver command, Session session) throws IOException, DataAccessException {
        AuthData auth;
        try {
            auth = authDAO.verify(command.getAuthString());
        } catch (Throwable e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Not Authorized")));
            return;
        }

        GameData gameToPrint = getGameToPrint(command.getGameID(), session);
        if (gameToPrint == null) return;

        connections.add(command.getAuthString(), command.getGameID(), session);
        var game = new LoadGameMessage(gameToPrint, ChessGame.TeamColor.WHITE);
        session.getRemote().sendString(new Gson().toJson(game));

        var message = String.format("%s joined the game as an observer", auth.username());
        var notification = new NotificationMessage(message);
        // Convert notification message to JSON
        String jsonMessage = new Gson().toJson(notification);
        connections.broadcast(command.getAuthString(), jsonMessage);
    }

    private GameData getGameToPrint(int gameID, Session session) throws DataAccessException, IOException {
        var games = gameDao.listGames();
        GameData gameToPrint = null;
        for (var game : games) {
            if (game.gameID() == gameID) {
                gameToPrint = game;
            }
        }
        if (gameToPrint == null) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Game does not exists")));
            return null;
        }
        return gameToPrint;
    }

    public void joinPlayer(JoinCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth;
        try {
            auth = authDAO.verify(command.getAuthString());
        } catch (Throwable e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Not Authorized")));
            return;
        }
        String teamColor;
        if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
            teamColor = "White";
        } else {
            teamColor = "Black";
        }

        GameData gameToPrint = getGameToPrint(command.getGameID(), session);
        if (gameToPrint == null) return;

        boolean spotTaken = false;
        ErrorMessage error = null;

        // Check if the WHITE team spot is already taken
        if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (gameToPrint.whiteUsername() == null || !gameToPrint.whiteUsername().equals(auth.username())) {
                spotTaken = true;
                error = new ErrorMessage("Team spot is already taken");
            }
        }
        // Check if the BLACK team spot is already taken
        else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (gameToPrint.blackUsername() == null || (!gameToPrint.blackUsername().equals(auth.username()))) {
                spotTaken = true;
                error = new ErrorMessage("Team spot is already taken");
            }
        }

        // If the spot is taken, send an error message
        if (spotTaken && session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }


        connections.add(command.getAuthString(), command.getGameID(), session);
        var game = new LoadGameMessage(gameToPrint, command.getTeamColor());
        session.getRemote().sendString(new Gson().toJson(game));

        var message = String.format("%s joined the game for the %s team", auth.username(), teamColor);
        var notification = new NotificationMessage(message);
        // Convert notification message to JSON
        String jsonMessage = new Gson().toJson(notification);
        connections.broadcast(command.getAuthString(), jsonMessage);
    }


}
