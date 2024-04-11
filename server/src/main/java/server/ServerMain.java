package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            var server = new Server();
            server.run(port);
            port = server.port();
            System.out.printf("Server started on port %d%n", port);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }
}