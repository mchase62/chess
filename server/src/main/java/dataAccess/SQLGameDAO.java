package dataAccess;

import chess.ChessGame;
import handler.ListGamesResponse;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class SQLGameDAO implements GameDAO{
    @Override
    public int createGame(String gameName) throws DataAccessException {
//        if(gameName.isEmpty())
//            return 0;
//        int gameID = newGameID();
//        GameData gameData = new GameData(gameID,null,null, gameName, new ChessGame());
//        gamesByID.put(gameID,gameData);
//        return gameID;
        return 0;
    }

    @Override
    public Collection<ListGamesResponse.GameItem> listGames() throws DataAccessException {
//        Collection<ListGamesResponse.GameItem> gamesList = new HashSet<>();
//
//        for (Map.Entry<Integer, GameData> entry : gamesByID.entrySet()) { // loop through games and convert to list
//            ListGamesResponse.GameItem gameItem = new ListGamesResponse.GameItem(entry.getKey(), entry.getValue().gameName(), entry.getValue().whiteUsername(), entry.getValue().blackUsername());
//            gamesList.add(gameItem);
//        }
//        return gamesList;
        return null;
    }

    @Override
    public String updateGame(String username, String playerColor, int gameID) throws DataAccessException {
//        GameData gameData = gamesByID.get(gameID); // get the game data object
//        if (gameData==null) // bad game id
//            return "bad request";
//        String white = gameData.whiteUsername();
//        String black = gameData.blackUsername();
//        if (playerColor == null) {
//            return "success";
//        }
//        if (playerColor.equals("WHITE") && white == null) // if player chose white and it's not taken
//            white = username;
//        else if (playerColor.equals("BLACK") && black == null) // if player chose black and it's not taken
//            black = username;
//        else // both are taken
//            return "already taken";
//        GameData updatedGameData = new GameData(gameID, white, black, gameData.gameName(), gameData.game()); // create updated game
//        gamesByID.put(gameID, updatedGameData);
//        return "success";
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
//        gamesByID.clear()
    }
}
