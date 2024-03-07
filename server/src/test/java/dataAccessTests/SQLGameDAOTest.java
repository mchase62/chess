package dataAccessTests;

import dataAccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest{
    private GameDAO getGameDAO(Class<? extends GameDAO> databaseClass) throws DataAccessException {
        GameDAO gameDAO;
        if(databaseClass.equals(SQLGameDAO.class)) {
            gameDAO = new SQLGameDAO();
        } else {
            gameDAO = new MemoryGameDAO();
        }
        return gameDAO;
    }

    @AfterEach
    public void cleanUp() throws DataAccessException { // empty game table after each run
        GameDAO gameDAO = getGameDAO(SQLGameDAO.class);
        gameDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameTestSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException { // tries to create game
        int id;
        GameDAO gameDAO = getGameDAO(gameDAOClass);
        id = gameDAO.createGame("new_game");
        assertNotEquals(0,id);
    }
    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameTestFail(Class<? extends GameDAO> gameDAOClass) throws DataAccessException { // tries to create game with empty string
        GameDAO gameDAO = getGameDAO(gameDAOClass);
            int id = gameDAO.createGame(""); // create the game
            assertEquals(0, id); // should equal 0
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void clearTest(Class<? extends GameDAO> gameDAOClass) throws DataAccessException { // creates a game in database and immediately clears database
        GameDAO gameDAO = getGameDAO(gameDAOClass);
        gameDAO.createGame("game_name");
        gameDAO.clear();
        assert(gameDAO.listGames().isEmpty()); // should be empty if games were deleted
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesTestSuccess(Class<? extends GameDAO> gameDAOClass) throws DataAccessException { // adds 2 games to database
        GameDAO gameDAO = getGameDAO(gameDAOClass);
        gameDAO.createGame("first_game_name");
        gameDAO.createGame("second_game_name");
        assertEquals(2,gameDAO.listGames().size());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesTestFail(Class<? extends GameDAO> gameDAOClass) throws DataAccessException { // game wasn't created so list should be empty
        GameDAO gameDAO = getGameDAO(gameDAOClass);
        gameDAO.createGame("");
        assert(gameDAO.listGames().isEmpty());
    }
}
