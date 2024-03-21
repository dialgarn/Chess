import chess.ChessGame;
import chess.ChessPiece;
import dataAccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var port = 8080;
        var server = new Server();

        if (args.length > 0 && args[0].equals("test")) {
            port = 0;
        }

        int curPort = server.run(port);
        System.out.println("Server running on port: " + curPort);
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
    }
}