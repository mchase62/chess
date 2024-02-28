package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    public int createGame(String gameName) throws DataAccessException;
    public Collection<ArrayList<Object>> listGames() throws DataAccessException;
    public String updateGame(String username, String playerColor, int gameID) throws DataAccessException;
    public void clear() throws DataAccessException;
}
