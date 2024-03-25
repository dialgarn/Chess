package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        String white = (whiteUsername != null) ? whiteUsername : "no player";
        String black = (blackUsername != null) ? blackUsername : "no player";

        return String.format("gameID= %d, White Username= \"%s\", Black Username= \"%s\", Game Name= %s, game= %s",
                gameID, white, black, gameName, game);
    }
}
