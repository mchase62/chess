package dataAccess;

import model.UserData;

public interface UserDAO extends DataAccess {
    void clear() throws DataAccessException;
    UserData register(UserData user) throws DataAccessException;
}

