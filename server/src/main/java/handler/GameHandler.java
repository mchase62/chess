package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import model.UserData;
import passoffTests.testClasses.TestModels;
import service.GameService;
import spark.Request;
import spark.Response;
import model.GameData;

import java.util.*;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handleNewGame(Request request, Response response) {
        try {
            GameData game = gson.fromJson(request.body(), GameData.class); // get game name
            String auth = request.headers("Authorization"); // get auth token
            if(game.gameName() == null) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request","Error: bad request"));
            }
            else if (authDAO.getUser(auth) == null) {
                response.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized", "Error: unauthorized"));
            }
            int gameID = gameService.createGame(game.gameName()); // create new game
            response.status(200); // code was successful
            return gson.toJson(new CreateGameResponse(gameID));
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleListGames(Request request, Response response) {
        try {
            String auth = request.headers("Authorization"); // get auth token
            if (authDAO.getUser(auth) == null) { // auth token doesn't exist
                response.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized", "Error: unauthorized"));
            }
            Collection<ListGamesResponse.GameItem> games = gameService.listGames();
            System.out.println(gson.toJson(new ListGamesResponse(games)));
            return gson.toJson(new ListGamesResponse(games));
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleJoinGame(Request request, Response response) {
        try {
            String status;
            String auth = request.headers("Authorization"); // get auth token
            String username = MemoryAuthDAO.getInstance().getUser(auth);
            if (username==null) { // if the user is not authorized
                response.status(401);
                status = "unauthorized";
                return gson.toJson(new ErrorResponse("Error: " + status, "Error: " + status));
            }
            GameRequest gameRequest= gson.fromJson(request.body(), GameRequest.class);
            status = gameService.joinGame(username, gameRequest.getPlayerColor(), gameRequest.getGameID());

            if(status.equals("already taken")) { // spot taken
                response.status(403);
                return gson.toJson(new ErrorResponse("Error: " + status, "Error: " + status));
            }
            else if (status.equals("bad request")) { // game id doesn't exist
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: " + status, "Error: " + status));
            }
            else { // request was successful
                response.status(200);
                return "";
            }

        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public static class GameRequest {
        private final String playerColor;
        private final int gameID;
        public GameRequest(String playerColor, int gameID) {
            this.playerColor = playerColor;
            this.gameID = gameID;
        }
        public String getPlayerColor() {
            return playerColor;
        }
        public int getGameID() {
            return gameID;
        }
    }
}
