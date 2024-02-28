package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.Map;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public String getUser(String auth) throws DataAccessException;
    public String deleteAuth(String auth) throws DataAccessException;
    public void clear() throws DataAccessException;
    public Map<String, String> getUsersByAuth() throws DataAccessException;
}
