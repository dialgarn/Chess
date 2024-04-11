package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinCommand extends UserGameCommand {

    private final ChessGame.TeamColor playerColor;
    private final int gameID;
    public JoinCommand(String authToken, ChessGame.TeamColor playerColor, int gameID) {
        super(authToken);
        this.playerColor = playerColor;
        this.gameID = gameID;
        setCommandType(CommandType.JOIN_PLAYER);
    }

    public ChessGame.TeamColor getTeamColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
