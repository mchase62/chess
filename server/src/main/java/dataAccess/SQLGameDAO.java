package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.ListGamesResponse;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO{
    public static SQLGameDAO instance;
    private int gameID;
    public SQLGameDAO() {
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
        try {
            DatabaseManager.configureDatabase(createStatements);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
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
                        GameData gameData = new GameData(gameID, null, null, gameName,chessGame);
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
        Collection<ListGamesResponse.GameItem> gamesList = new HashSet<>();
        String gameJson;
        GameData gameData;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameJson = rs.getString("game_json");
                        gameData = new Gson().fromJson(gameJson, GameData.class);//entry.getKey(), entry.getValue().gameName(), entry.getValue().whiteUsername(), entry.getValue().blackUsername()
                        ListGamesResponse.GameItem gameItem = new ListGamesResponse.GameItem(gameData.gameID(), gameData.gameName(), gameData.whiteUsername(),gameData.blackUsername());
                        gamesList.add(gameItem);
                    }
                    return gamesList;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String updateGame(String username, String playerColor, int gameID) throws DataAccessException {
        GameData gameData;
        String white;
        String black;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_json FROM game WHERE game_id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1,gameID);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) { // if game_json returned null, the gameID doesn't exist
                        return "bad request";
                    }
                    else if (playerColor == null) {
                        return "success";
                    }
                    else {
                        String gameJson = rs.getString("game_json");
                        gameData = new Gson().fromJson(gameJson, GameData.class);//entry.getKey(), entry.getValue().gameName(), entry.getValue().whiteUsername(), entry.getValue().blackUsername()
                        white = gameData.whiteUsername();
                        black = gameData.blackUsername();
                        if (playerColor.equals("WHITE") && white == null) // if player chose white and it's not taken
                            white = username;
                        else if (playerColor.equals("BLACK") && black == null) // if player chose black and it's not taken
                            black = username;
                        else
                            return "already taken";
                        GameData newGameData = new GameData(gameData.gameID(), white, black, gameData.gameName(), gameData.game());
                        var json = new Gson().toJson(newGameData);
                        var updateStatement = "UPDATE game SET white_username = ?, black_username = ?, game_json =? where game_id = ?";
                        DatabaseManager.executeUpdate(updateStatement, white, black, json, gameID); // returns 0 if game was not made
                        return "success";
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                ps.execute();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
