package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final int gameID;
    public MakeMoveCommand(String authToken, ChessMove move, int gameID) {
        super(authToken);
        setCommandType(CommandType.MAKE_MOVE);
        this.move = move;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
