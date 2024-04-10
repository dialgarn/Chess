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
import service.AuthService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinCommand;
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
                switch (command.getCommandType()) {
                    case JOIN_PLAYER:
                        // Handle join player command
                        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                        String playerColorValue = jsonObject.get("playerColor").getAsString();
                        ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(playerColorValue.toUpperCase());

                        String auth = jsonObject.get("authToken").getAsString();
                        int gameID = jsonObject.get("gameID").getAsInt();

                        JoinCommand joinCommand = new JoinCommand(auth, color, gameID);
                        joinPlayer(joinCommand, session);
                        break;
                    case JOIN_OBSERVER:
                        // Handle join observer command
                        joinObserver(command, session);
                        break;
                    case MAKE_MOVE:
                        // Handle make move command
                        makeMove(command, session);
                        break;
                }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(UserGameCommand command, Session session) {
    }

    public void joinObserver(UserGameCommand command, Session session) {
    }

    public void joinPlayer(JoinCommand command, Session session) throws IOException, DataAccessException {
        connections.add(command.getAuthString(), command.getGameID(), session);
        AuthData auth = authDAO.verify(command.getAuthString());

        String teamColor = "";
        if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
            teamColor = "White";
        } else {
            teamColor = "Black";
        }

        var games = gameDao.listGames();
        GameData gameToPrint = null;
        for (var game : games) {
            if (game.gameID() == command.getGameID()) {
                gameToPrint = game;
            }
        }
        assert gameToPrint != null;

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

        var game = new LoadGameMessage(gameToPrint, command.getTeamColor());
        session.getRemote().sendString(new Gson().toJson(game));

        var message = String.format("%s joined the game for the %s team", auth.username(), teamColor);
        var notification = new NotificationMessage(message);
        // Convert notification message to JSON
        String jsonMessage = new Gson().toJson(notification);
        connections.broadcast(command.getAuthString(), jsonMessage);
    }


}
