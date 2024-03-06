package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    void createUserTest(Class<? extends UserDAO> userDAOClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(userDAOClass);

        var user = new UserData("new_username", "new_password", "new_email");
        assertDoesNotThrow(() -> userDAO.createUser(user));
    }
}
