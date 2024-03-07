package service;

import dataAccess.*;
public class ClearService {
    public void clearData() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        GameDAO gameDAO = new SQLGameDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }
}
