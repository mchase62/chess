package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.ListGamesResponse;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class SQLGameDAO implements GameDAO{
    private int gameID;
    public SQLGameDAO() throws DataAccessException {
        String[] createStatements = { // whiteUsername, String blackUsername, String gameName, ChessGame game
            """
            CREATE TABLE IF NOT EXISTS game (
            id int NOT NULL AUTO_INCREMENT,
            game_id int NOT NULL,
            game_name varchar(256) NOT NULL,
            white_username varchar(256),
            black_username varchar(256),
            game_json varchar(256),
            PRIMARY KEY (id)
            );
            """
        };
        DatabaseManager.configureDatabase(createStatements);
        this.gameID = 0;
    }

    public int newGameID() {
        gameID += 1;
        return gameID;
    }
    @Override
    public int createGame(String gameName) throws DataAccessException {
        if(gameName.isEmpty())
            return 0;
        int gameID = newGameID();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_name FROM game WHERE game_name=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,gameName);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) { // if game returned null, it doesn't already exist with that gameName
                        ChessGame chessGame = new ChessGame();
                        GameData gameData = new GameData(gameID, "", "", gameName,chessGame);
                        var json = new Gson().toJson(gameData);
                        var insert_statement = "INSERT INTO game (game_id, game_name, game_json) values (?,?,?)";
                        int returnedGameID =  DatabaseManager.executeUpdate(insert_statement, gameID, gameName, json); // returns 0 if game was not made
                        if (returnedGameID != 0) // return the game id if successful
                            return gameID;
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                ps.execute();
            }
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
