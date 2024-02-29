package serviceTests;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.ClearService;
public class ClearServiceTest {
    static final ClearService service = new ClearService();

    @Test
    void clear() throws DataAccessException {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();

        // fill data
        UserData user = new UserData("testUsername", "testPassword", "testEmail");
        gameDAO.createGame("gameName");
        AuthData auth = authDAO.createAuth(user.username());

        service.clearData();

        // Assert: Check that DAO objects are cleared
        assertNull(authDAO.getUser(auth.authToken()), "AuthDAO should be empty after clearData");
        assertTrue(gameDAO.listGames().isEmpty(), "GameDAO should be empty after clearData");
        assertNull(userDAO.getUser(user.username()), "UserDAO should be empty after clearData");
    }
}
