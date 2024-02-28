package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    public static MemoryGameDAO instance;
    private int gameID;
    private final Map<Integer, GameData> gamesByID = new HashMap<>();

    public MemoryGameDAO() {
        this.gameID = 0;
    }

    public static synchronized MemoryGameDAO getInstance() {
        if(instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    public int newGameID() {
        gameID += 1;
        return gameID;
    }

    public int createGame(String gameName) {
        int gameID = newGameID();
        GameData gameData = new GameData(gameID,"","", gameName, new ChessGame());
        gamesByID.put(gameID,gameData);
        return gameID;
    }

    public Map<Integer, GameData> listGames() {
        return gamesByID;
    }

    public String updateGame(String playerColor, int gameID) {
        GameData gameData = gamesByID.get(gameID); // get the game data object
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (playerColor.equals("white") && white.isEmpty()) // if player chose white and it's not taken
            white = "username";
        else if (playerColor.equals("black") && black.isEmpty()) // if player chose black and it's not taken
            black = "username";
        else
            return "already taken";
        GameData updatedGameData = new GameData(gameID, white, black, gameData.gameName(),gameData.game()); // create updated game
        gamesByID.put(gameID, updatedGameData);
        return "success";
    }
}
