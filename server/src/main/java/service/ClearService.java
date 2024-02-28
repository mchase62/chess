package service;

import dataAccess.*;
public class ClearService {
    private final UserDAO userDAO = MemoryUserDAO.getInstance();
    private final GameDAO gameDAO = MemoryGameDAO.getInstance();
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();
    public void clearData() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
