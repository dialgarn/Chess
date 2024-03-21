package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GameRequests {

    public void createGame(String gameName, String authToken, String url) throws DataAccessException {
        String requestBody = new Gson().toJson(Map.of(
                "gameName", gameName
        ));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject responseBodyJson = new Gson().fromJson(response.body(), JsonObject.class);
            if (response.statusCode() == 200) {

                int gameID =  responseBodyJson.get("gameID").getAsInt();
                System.out.printf("Successfully created a game with game name: \"%s\" and gameID: %d%n", gameName, gameID);
            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameID, String teamColor, String url) throws DataAccessException {
        String requestBody;
        if (teamColor != null) {
            requestBody = new Gson().toJson(Map.of(
                    "playerColor", teamColor,
                    "gameID", gameID
            ));
        } else {
            requestBody = new Gson().toJson(Map.of(
                    "gameID", gameID
            ));
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ArrayList<GameData> games = (ArrayList<GameData>) getGames(authToken, url);
                GameData gameToPrint = null;
                for (var game : games) {
                    if (game.gameID() == gameID) {
                        gameToPrint = game;
                    }
                }
                assert gameToPrint != null;
                System.out.println(gameToPrint.game().getBoard().realToString());

            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<GameData> getGames(String authToken, String url) throws DataAccessException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject responseBodyJson = new Gson().fromJson(response.body(), JsonObject.class);
            if (response.statusCode() == 200) {
                ArrayList<GameData> games = new ArrayList<>();
                JsonArray gamesArray = responseBodyJson.getAsJsonArray("games");
                for (JsonElement gameElement : gamesArray) {
                    GameData gameData = getGameData(gameElement);
                    games.add(gameData);
                }
                return games;
            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private GameData getGameData(JsonElement gameElement) {
        JsonObject gameObject = gameElement.getAsJsonObject();
        int gameID = gameObject.get("gameID").getAsInt();
        String whiteUsername = gameObject.has("whiteUsername") ? gameObject.get("whiteUsername").getAsString() : null;
        String blackUsername = gameObject.has("blackUsername") ? gameObject.get("blackUsername").getAsString() : null;

        String gameName = gameObject.get("gameName").getAsString();
        // Deserialize the game object
        JsonObject gameJson = gameObject.getAsJsonObject("game");
        // Assuming you have a method to deserialize ChessGame from JsonObject
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        // Assuming you have a constructor in GameData that takes gameID and gameName
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

}
