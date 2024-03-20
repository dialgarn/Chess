package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class GameRequests {

    int responseValue;

    public void createGame(String gameName, String authToken, String url) throws Exception {
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
                responseValue = response.statusCode();
                int gameID =  responseBodyJson.get("gameID").getAsInt();
                System.out.printf("Successfully created a game with game name: \"%s\" and gameID: %d%n", gameName, gameID);
            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new Exception(e.getMessage());
        }
    }


}
