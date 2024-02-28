package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, String> usersByAuth = new HashMap<>();
    private static MemoryAuthDAO instance = null;

    public static synchronized MemoryAuthDAO getInstance() {
        if(instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        usersByAuth.put(auth, username);
        return new AuthData(auth, username);
    }

    @Override
    public String deleteAuth(String auth) throws DataAccessException {
        if(usersByAuth.containsKey(auth)) {
            usersByAuth.remove(auth);
            return "success";
        }
        else {
            return "fail";
        }
    }

    @Override
    public String getUser(String auth) throws DataAccessException {
        return usersByAuth.getOrDefault(auth, null);
    }
}
