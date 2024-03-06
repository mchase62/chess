package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {
    private UserDAO getUserDAO(Class<? extends UserDAO> databaseClass) throws DataAccessException {
        UserDAO userDAO;
        if(databaseClass.equals(SQLUserDAO.class)) {
            userDAO = new SQLUserDAO();
        } else {
            userDAO = new MemoryUserDAO();
        }
        userDAO.clear();
        return userDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserTestSuccess(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);

        var user = new UserData("new_username", "new_password", "new_email");
        assertEquals("Success", userDAO.createUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class}) // test for same username
    void createUserTestFail(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);
        var first_user = new UserData("new_username", "new_password", "first_email");
        userDAO.createUser(first_user);
        var second_user = new UserData("new_username", "new_password", "second_email");
        assertEquals("Fail", userDAO.createUser(second_user));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserTestSuccess(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);
        // add user
        var user = new UserData("get_username", "new_password", "new_email");
        userDAO.createUser(user);

        var returned_user = userDAO.getUser(user.username());

        assertEquals(user.username(),returned_user.username());
        assertEquals(user.email(), returned_user.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserTestFail(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);

        var returned_user = userDAO.getUser("not_a_username");

        assertNull(returned_user);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getPasswordTestSuccess(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);

        // add user
        var user = new UserData("password_test_username", "new_password", "new_email");
        userDAO.createUser(user);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        var hashedPassword = userDAO.getPassword("password_test_username");
        if(userDAOClass.isAssignableFrom(SQLUserDAO.class)) // if we stored and hashed the password in SQL
            assertTrue(encoder.matches(user.password(), hashedPassword));
        else
            assertEquals(user.password(), hashedPassword);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getPasswordTestFail(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);

        var hashedPassword = userDAO.getPassword("not_a_real_username");
        assertNull(hashedPassword);
    }

}
