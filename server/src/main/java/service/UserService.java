package service;
import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        userDAO = MemoryUserDAO.getInstance();
        authDAO = MemoryAuthDAO.getInstance();
    }

    public String createUser(UserData user) throws DataAccessException {
        return userDAO.createUser(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        return authDAO.createAuth(username);
    }

    public AuthData login(String username, String password) throws DataAccessException {
        String userPassword = userDAO.getPassword(username);
        if (password.equals(userPassword)) {
            return authDAO.createAuth(username);
        }
        return null;
    }

    public String logout(String auth) throws DataAccessException {
        return authDAO.deleteAuth(auth);
    }
}
