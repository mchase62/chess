package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
public class ClearService {
    private final UserDAO userDAO = MemoryUserDAO.getInstance();
    public void clearData() throws DataAccessException {
        userDAO.clear();
    }
}
