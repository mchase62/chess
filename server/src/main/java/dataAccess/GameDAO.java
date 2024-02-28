package dataAccess;

import model.GameData;

import java.util.List;
import java.util.Map;

public interface GameDAO {
    public int createGame(String gameName) throws DataAccessException;
    public Map<Integer, GameData> listGames() throws DataAccessException;
    public String updateGame(String username, String playerColor, int gameID) throws DataAccessException;
}
