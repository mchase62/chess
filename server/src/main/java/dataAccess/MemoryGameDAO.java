package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

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

    @Override
    public int createGame(String gameName) {
        int gameID = newGameID();
        GameData gameData = new GameData(gameID,"","", gameName, new ChessGame());
        gamesByID.put(gameID,gameData);
        return gameID;
    }

    @Override
    public Collection<ArrayList<Object>> listGames() {
        Collection<ArrayList<Object>> gamesList = new HashSet<>();

        for (Map.Entry<Integer, GameData> entry : gamesByID.entrySet()) { // loop through games and convert to list
            ArrayList<Object> game = new ArrayList<Object>();
            game.add(entry.getKey());
            game.add(entry.getValue().gameName());
            game.add(entry.getValue().blackUsername());
            game.add(entry.getValue().whiteUsername());
            gamesList.add(game);
        }
        return gamesList;
    }

    @Override
    public String updateGame(String username, String playerColor, int gameID) {
        GameData gameData = gamesByID.get(gameID); // get the game data object
        if (gameData==null) // bad game id
            return "bad request";
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (playerColor.equals("WHITE") && white.isEmpty()) // if player chose white and it's not taken
            white = username;
        else if (playerColor.equals("BLACK") && black.isEmpty()) // if player chose black and it's not taken
            black = username;
        else // both are taken
            return "already taken";
        GameData updatedGameData = new GameData(gameID, white, black, gameData.gameName(), gameData.game()); // create updated game
        gamesByID.put(gameID, updatedGameData);
        return "success";
    }

    @Override
    public void clear() {
        gamesByID.clear();
    }
}
