package ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class UserRequests {

    int responseValue;
    String authToken;


    public String register(String username, String password, String email, String url) throws DataAccessException {
        String requestBody = new Gson().toJson(Map.of(
                "username", username,
                "password", password,
                "email", email
        ));

        try {
            authToken = makeHttpUserPostRequest(requestBody, url);
            if (responseValue == 200) {
                System.out.println("Successfully registered and logged in.");
                return authToken;
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public String login(String username, String password, String url) throws DataAccessException {
        String requestBody = new Gson().toJson(Map.of(
                "username", username,
                "password", password
        ));

        try {
            authToken = makeHttpUserPostRequest(requestBody, url);
            if (responseValue == 200) {
                System.out.println("Successfully logged in.");
                return authToken;
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void logout(String url) throws DataAccessException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                responseValue = response.statusCode();
                System.out.println("Successfully logged out.");
            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    private String makeHttpUserPostRequest(String requestBody, String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject responseBodyJson = new Gson().fromJson(response.body(), JsonObject.class);
            if (response.statusCode() == 200) {
                responseValue = response.statusCode();
                return responseBodyJson.get("authToken").getAsString();

            } else {
                throw new DataAccessException(response.body());
            }
        } catch (Throwable e) {
            throw new Exception(e.getMessage());
        }
    }
}
