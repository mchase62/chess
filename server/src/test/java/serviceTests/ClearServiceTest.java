package serviceTests;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.ClearService;
public class ClearServiceTest {
    static final ClearService service = new ClearService();

    @Test
    void clear() throws DataAccessException {
        service.clearData();
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        // Assert: Check that DAO objects are cleared
        assertTrue(authDAO.getUsersByAuth().isEmpty(), "AuthDAO should be empty after clearData");
        assertTrue(gameDAO.listGames().isEmpty(), "GameDAO should be empty after clearData");
        assertTrue(userDAO.getUsers().isEmpty(), "UserDAO should be empty after clearData");
    }
}
