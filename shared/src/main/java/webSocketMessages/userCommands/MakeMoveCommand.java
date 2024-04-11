package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final int gameID;
    // private final ChessGame.TeamColor playerColor;
    public MakeMoveCommand(String authToken, ChessMove move, int gameID) {
        super(authToken);
        setCommandType(CommandType.MAKE_MOVE);
        this.move = move;
        this.gameID = gameID;
        // this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

//    public ChessGame.TeamColor getPlayerColor() {
//        return playerColor;
//    }
}
