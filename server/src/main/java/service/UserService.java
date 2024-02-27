package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void createUser(UserData user) throws DataAccessException {
        userDAO.createUser(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public String createAuth(String username) throws DataAccessException {
        return UUID.randomUUID().toString();
    }
}
