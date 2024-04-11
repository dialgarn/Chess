package server.webSocket;

import chess.ChessGame;
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
import webSocketMessages.userCommands.JoinCommand;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.LeaveCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

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
                        makeMove(command, session);
                        break;
                    case LEAVE:
                        // Handle leave command
                        LeaveCommand leaveCommand = new LeaveCommand(auth);
                        leave(leaveCommand, session);
                }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println(e.getMessage());
        }
    }

    public void leave(LeaveCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.verify(command.getAuthString());
        NotificationMessage notification = new NotificationMessage(String.format("%s left the game", auth.username()));
        connections.broadcast(command.getAuthString(), new Gson().toJson(notification));
        connections.remove(command.getAuthString());
    }

    public void makeMove(UserGameCommand command, Session session) {
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
