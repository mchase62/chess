package dataAccessTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private AuthDAO getAuthDAO(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO authDAO;
        if(databaseClass.equals(SQLAuthDAO.class)) {
            authDAO = new SQLAuthDAO();
        } else {
            authDAO = new MemoryAuthDAO();
        }
        return authDAO;
    }


    @AfterEach
    public void cleanUp() throws DataAccessException { // empty user table after each run
        AuthDAO authDAO = getAuthDAO(SQLAuthDAO.class);
        authDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthTestSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException { // tries to create auth
        AuthDAO authDAO = getAuthDAO(authDAOClass);
        AuthData returnedAuth;
        var user = new UserData("auth_username", "auth_password", "auth_email");

        if (SQLAuthDAO.class.isAssignableFrom(authDAOClass)) { // run SQL test
            // add user to user database
            var createUserStatement = "INSERT INTO user (username, password, email) values (?, ?,?) ";
            DatabaseManager.executeUpdate(createUserStatement, user.username(), user.password(), user.email());

            // try to create auth for created user
            returnedAuth = authDAO.createAuth(user.username());

            // delete from user database
            var deleteUserStatement = "DELETE FROM user";

            DatabaseManager.executeUpdate(deleteUserStatement);
        }
        else {
            // add user to memory
            MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
            userDAO.createUser(user);

            returnedAuth = authDAO.createAuth(user.username());

            // delete user from memory
            userDAO.clear();
        }
        assertNotNull(returnedAuth); // returns an authData object if created
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthTestFail(Class<? extends AuthDAO> authDAOClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(authDAOClass);
        AuthData returnedAuth;
        if (SQLAuthDAO.class.isAssignableFrom(authDAOClass)) { // run SQL test
            // try to create auth for nonexistent user
            returnedAuth = authDAO.createAuth("not_a_username");
        }
        else {
            returnedAuth = authDAO.createAuth("not_a_username");
        }
        assertNull(returnedAuth); // returns an authData object if created
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthTestSuccess(Class<? extends AuthDAO> authDAOClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(authDAOClass);
        AuthData returnedAuth;
        String status;
        var user = new UserData("auth_username", "auth_password", "auth_email");

        if (SQLAuthDAO.class.isAssignableFrom(authDAOClass)) { // run SQL test
            // add user to user database
            var createUserStatement = "INSERT INTO user (username, password, email) values (?, ?,?) ";
            DatabaseManager.executeUpdate(createUserStatement, user.username(), user.password(), user.email());

            // create auth for created user
            returnedAuth = authDAO.createAuth(user.username());

            // delete user from user database
            var deleteUserStatement = "DELETE FROM user";

            status = authDAO.deleteAuth(returnedAuth.authToken()); // delete auth
            DatabaseManager.executeUpdate(deleteUserStatement);
        }
        else {
            // add user to memory
            MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
            userDAO.createUser(user);

            returnedAuth = authDAO.createAuth(user.username());

            status = authDAO.deleteAuth(returnedAuth.authToken());

            // delete user from memory
            userDAO.clear();
        }
        assertEquals("success", status);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthTestFail(Class<? extends AuthDAO> authDAOClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(authDAOClass);
        AuthData returnedAuth;
        String status;
        var user = new UserData("auth_username", "auth_password", "auth_email");

        if (SQLAuthDAO.class.isAssignableFrom(authDAOClass)) { // run SQL test
            // add user to user database
            var createUserStatement = "INSERT INTO user (username, password, email) values (?, ?,?) ";
            DatabaseManager.executeUpdate(createUserStatement, user.username(), user.password(), user.email());

            // create auth for created user
            returnedAuth = authDAO.createAuth(user.username());

            // delete user from user database
            var deleteUserStatement = "DELETE FROM user";

            status = authDAO.deleteAuth("wrong_auth"); // delete auth
            String returnedUser = authDAO.getUser(returnedAuth.authToken()); // get original
            DatabaseManager.executeUpdate(deleteUserStatement); // remove user from user database
            assertEquals(user.username(),returnedUser); // check if the user still existed
        }
        else {
            // add user to memory
            MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
            userDAO.createUser(user);

            returnedAuth = authDAO.createAuth(user.username());

            status = authDAO.deleteAuth("wrong_auth");

            // delete user from memory
            userDAO.clear();
            assertEquals("fail", status);
        }
    }
}
