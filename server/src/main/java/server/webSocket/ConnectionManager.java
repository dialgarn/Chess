package server.webSocket;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
// import webSocketMessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> games = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        games.put(authToken, gameID);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
        games.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, String notification) {
        var removeList = new ArrayList<String>(); // Use authToken for the removal list

        for (var entry : connections.entrySet()) {
            String authToken = entry.getKey();
            Connection c = entry.getValue();
            try {
                Integer gameID = games.get(authToken); // Retrieve gameID directly using authToken
                if (gameID != null && c.session.isOpen() && !authToken.equals(excludeAuthToken)) {
                    c.send(notification); // Send notification if criteria met
                } else if (!c.session.isOpen()) {
                    removeList.add(authToken); // Mark for removal if session is closed
                }
            } catch (IOException e) {
                System.err.println("Error broadcasting message to " + authToken + ": " + e.getMessage());
                removeList.add(authToken); // Optionally mark for removal on IOException
            }
        }

        // Clean up connections marked for removal
        removeList.forEach(this::remove); // Use ConnectionManager's remove method for cleanup
    }


}
