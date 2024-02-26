package service;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {
    private final UserDAO userDao;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public UserData register(UserData user) throws DataAccessException {
        return userDao.register(user);
    }
}
