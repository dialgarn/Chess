package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        return String.format("gameID=%d, whiteUsername=%s, blackUsername=%s, gameName=%s, game=%s",
                gameID, whiteUsername, blackUsername, gameName, game);
    }
}
