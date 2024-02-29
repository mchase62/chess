package passoffTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.*;
import handler.ListGamesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameService gameService = new GameService();
    static final ClearService clearService = new ClearService();
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = MemoryUserDAO.getInstance();
        authDAO = MemoryAuthDAO.getInstance();
        gameDAO = MemoryGameDAO.getInstance();
        clearService.clearData();
    }

    @Nested
    class CreateGameTest {
        @Test
        public void createGameSuccess() throws DataAccessException { // create valid game
            int id = gameService.createGame("New Game");
            assertNotEquals(0,id);
        }
        @Test
        public void createGameFail() throws DataAccessException { // No name given
            int id = gameService.createGame("");
            assertEquals(0, id);
        }
    }

    @Nested
    class ListGamesTest {
        @Test
        public void listGamesSuccess() throws DataAccessException { // successful list games returned not null
            int id = gameService.createGame("New Game");
            Object gamesList = gameService.listGames();
            assertNotNull(gamesList);
        }
        @Test
        public void listGamesFail() throws DataAccessException { // game wasn't created so list should be empty
            int id = gameService.createGame("");
            Collection<ListGamesResponse.GameItem> gamesList = gameService.listGames();
            assertTrue(gamesList.isEmpty());
        }
    }
    @Nested
    class JoinGameTest {
        @Test
        public void joinGamesSuccess() throws DataAccessException { // create new game with valid username
            int gameID = gameService.createGame("New Game");
            String response = gameService.joinGame("new_username","WHITE", gameID);
            assertEquals(response,"success");
        }
        @Test
        public void joinGamesFail() throws DataAccessException { // create new game with color already taken
            int gameID = gameService.createGame("New Game");
            gameService.joinGame("first_username","WHITE", gameID);
            String response = gameService.joinGame("second_username","WHITE", gameID);
            assertEquals(response,"already taken");
        }
    }
}
