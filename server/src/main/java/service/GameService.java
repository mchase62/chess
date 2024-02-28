package service;

import dataAccess.*;

public class GameService {
    private final GameDAO gameDAO;

    public GameService() {
        gameDAO = MemoryGameDAO.getInstance();
    }

    public int createGame(String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }
}
