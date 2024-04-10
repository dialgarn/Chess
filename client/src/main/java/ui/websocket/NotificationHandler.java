package ui.websocket;

import webSocketMessages.serverMessages.*;


public class NotificationHandler {
    public void notify(ServerMessage notification) {
        System.out.println(notification);
        printPrompt();
    }

    private void printPrompt() {

    }
}
