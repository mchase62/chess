package passoffTests.serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

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
        public void createUserSuccess() throws DataAccessException { // check if user is created
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            UserData returnUser = userService.getUser(newUser.username());
            assertEquals(newUser, returnUser);
        }
        @Test
        public void createUserFail() throws DataAccessException { // check if creating user fails when username already exists
            UserData firstUser = new UserData("my_username","my_password", "my_email");
            UserData secondUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(firstUser);
            String result = userService.createUser(secondUser);
            assertEquals(result,"Fail");
        }
    }
    @Nested
    class GetUserTest {
        @Test
        public void getUserSuccess() throws DataAccessException {
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            UserData returnUser = userService.getUser(newUser.username());
            assertNotNull(returnUser);
        }
        @Test
        public void getUserFail() throws DataAccessException {
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            UserData returnUser = userService.getUser("not_username");
            assertNull(returnUser);
        }
    }
    @Nested
    class LoginTest {
        @Test
        public void loginSuccess() throws DataAccessException { // check for log in success
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            AuthData authData = userService.login(newUser.username(),newUser.password());
            assertNotNull(authData);
        }
        @Test
        public void loginFail() throws DataAccessException { // check if log in fails with wrong password
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            AuthData authData = userService.login(newUser.username(),"not_password");
            assertNull(authData);
        }
    }
    @Nested
    class LogoutTest {
        @Test
        public void logoutSuccess() throws DataAccessException { // check for log out success
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            AuthData authData = userService.login(newUser.username(), newUser.password());
            String result = userService.logout(authData.authToken());
            assertEquals(result,"success");
        }
        @Test
        public void logoutFail() throws DataAccessException { // check for log out fail
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            AuthData authData = userService.login(newUser.username(), newUser.password());
            String result = userService.logout("Not authToken");
            assertEquals(result,"fail");
        }
    }
    @Nested
    class CreateAuthTest {
        @Test
        public void createAuthSuccess() throws DataAccessException { // check for creating auth
            UserData newUser = new UserData("my_username","my_password", "my_email");
            userService.createUser(newUser);
            String result = userService.createAuth(newUser.username()).authToken();
            assertNotNull(result);
        }
        @Test
        public void createAuthFail() throws DataAccessException { // fail because user doesn't exist
            AuthData result = userService.createAuth("fake_username");
            assertNull(result);
        }
    }

}
