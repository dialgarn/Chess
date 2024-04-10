package webSocketMessages.serverMessages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {

    private final GameData game;
    private final ChessGame.TeamColor teamColor;
    public LoadGameMessage(GameData game, ChessGame.TeamColor teamColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.teamColor = teamColor;
    }

    public GameData getGame() {
        return game;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
