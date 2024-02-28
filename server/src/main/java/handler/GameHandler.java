package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;
import model.GameData;

import java.util.List;
import java.util.Map;

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
            Map<Integer, GameData> gamesMap = gameService.listGames();
            return gson.toJson(new ListGamesResponse(gamesMap));
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleJoinGame(Request request, Response response) {
        try {
            String auth = request.headers("Authorization"); // get auth token
            GameRequest gameRequest= gson.fromJson(request.body(), GameRequest.class);
            String status = gameService.joinGame(gameRequest.getPlayerColor(), gameRequest.getGameID());
            if(status.equals("already taken")) {
                response.status(403);
                return gson.toJson(new ErrorResponse("Error: " + status, "Error: " + status));
            }
            return "";
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
