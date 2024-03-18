package ui;

import server.Server;

import java.util.Scanner;

public class Client {
    public void run(String[] args) {
        var server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            if (line.equals("help")) {
                System.out.println("yay it worked");
            } else if (line.equals("quit")) {
                break;
            }

        }
        server.stop();
    }
}
