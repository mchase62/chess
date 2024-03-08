package service;

import dataAccess.*;
import handler.ListGamesResponse;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService() {
        gameDAO = new SQLGameDAO();
    }

    public int createGame(String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }

    public Collection<ListGamesResponse.GameItem> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public String joinGame(String username, String playerColor, int gameID) throws DataAccessException {
        return gameDAO.updateGame(username, playerColor, gameID);
    }
}
