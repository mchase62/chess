package server;

import exception.ResponseException;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public UserData register(UserData user) throws ResponseException { // returns user with username
        var path = "/user";
        return this.makeRequest("POST", path, user, UserData.class, null);
    }

    public AuthData login(UserData user) throws ResponseException { // returns auth token
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public Object logout(String auth) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, null, auth);
    }

    public GameData createGame(GameData game, String auth) throws ResponseException { // returns gameData
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class, auth);
    }

    public GameData joinGame(String playerColor, int gameID, String auth) throws ResponseException {
        var path = "/game";
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("playerColor", playerColor);
        jsonMap.put("gameID", gameID);
        return this.makeRequest("PUT", path, jsonMap, GameData.class, auth);
    }

    public GameData[] listGames(String auth) throws ResponseException {
        var path = "/game";
        record ListGameResponse(GameData[] games, String[] whiteUsernames, String[] blackUsernames) {

        }
        var response = this.makeRequest("GET", path, null, ListGameResponse.class, auth);
        return response.games();
    }
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String header) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Add Authorization header
            http.setRequestProperty("Authorization", header);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }
}