package dataAccessTests;

import dataAccess.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    public void cleanUp() throws DataAccessException { // empty user table after each run
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
}
