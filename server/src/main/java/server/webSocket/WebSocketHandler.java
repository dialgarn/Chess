package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Session;
import webSocketMessages.userCommands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // Deserialize the incoming JSON message into a command object
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                switch (command.getCommandType()) {
                    case JOIN_PLAYER:
                        // Handle join player command
                        joinPlayer(command, session);
                        break;
                    case JOIN_OBSERVER:
                        // Handle join observer command
                        joinObserver(command, session);
                        break;
                    case MAKE_MOVE:
                        // Handle make move command
                        makeMove(command, session);
                        break;
                    
                    default:
                        // Unknown command type
                        sendError("Unknown command type");
                }
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            // Send error response
            sendError("Error processing message");
        }
    }

    public void sendError(String unknownCommandType) {
    }

    public void makeMove(UserGameCommand command, Session session) {
    }

    public void joinObserver(UserGameCommand command, Session session) {
    }

    public void joinPlayer(UserGameCommand command, Session session) {
    }

}
