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

public class GameHandler {
    private GameService gameService;
    private final Gson gson = new Gson();
    private AuthDAO authDAO = MemoryAuthDAO.getInstance();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handleNewGame(Request request, Response response) {
        try {
            GameData game = gson.fromJson(request.body(), GameData.class);
            System.out.println(request.body());
            String auth = request.headers("Authorization");
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
}
