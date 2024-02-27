package service;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void createUser(UserData user) throws DataAccessException {
        userDAO.createUser(user);
    }

}
