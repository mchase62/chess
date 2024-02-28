package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
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
}
