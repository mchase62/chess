package passoffTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceTest {
    static final UserService userService = new UserService();
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = MemoryUserDAO.getInstance();
        authDAO = MemoryAuthDAO.getInstance();
        userDAO.clear();
        authDAO.clear();
    }
    @Nested
    class CreateUserTest {
        @Test
        public void createUserSuccess() throws DataAccessException {
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            UserData returnUser = userService.getUser(newUser.username());
            assertEquals(newUser, returnUser);
        }
        @Test
        public void createUserFail() throws DataAccessException {
            UserData firstUser = new UserData("my_username","my_password", "my_email");
            UserData secondUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(firstUser);
            String status = userService.createUser(secondUser);
            assertEquals(status,"Fail");
        }
    }


}
