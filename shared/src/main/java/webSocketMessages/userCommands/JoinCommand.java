package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinCommand extends UserGameCommand {

    private final ChessGame.TeamColor teamColor;
    private final int gameID;
    public JoinCommand(String authToken, ChessGame.TeamColor teamColor, int gameID) {
        super(authToken);
        this.teamColor = teamColor;
        this.gameID = gameID;
        setCommandType(CommandType.JOIN_PLAYER);
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public int getGameID() {
        return gameID;
    }
}
