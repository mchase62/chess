package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;

    public String deleteAuth(String auth) throws DataAccessException;
}
