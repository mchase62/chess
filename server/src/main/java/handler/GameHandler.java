package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.GameService;
import spark.Request;
import spark.Response;
import model.GameData;

public class GameHandler {
    private GameService gameService;
    private final Gson gson = new Gson();


    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handleNewGame(Request request, Response response) {
        try {
            GameData game = gson.fromJson(request.body(), GameData.class);
            if(game.gameName() == null) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request","Error: bad request"));
            }
            int gameID = gameService.createGame(game.gameName()); // create new game
            response.status(200); // code was successful
            return gson.toJson(new CreateGameResponse(gameID));
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }
}
