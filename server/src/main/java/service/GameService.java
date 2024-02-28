package service;

import dataAccess.*;
import model.GameData;

import java.util.List;
import java.util.Map;

public class GameService {
    private final GameDAO gameDAO;

    public GameService() {
        gameDAO = MemoryGameDAO.getInstance();
    }

    public int createGame(String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }

    public Map<Integer, GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public String joinGame(String username, String playerColor, int gameID) throws DataAccessException {
        return gameDAO.updateGame(username, playerColor, gameID);
    }
}
