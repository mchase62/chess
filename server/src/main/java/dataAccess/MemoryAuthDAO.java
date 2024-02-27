package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, String> usersByAuth = new HashMap<>();
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        usersByAuth.put(auth, username);
        return new AuthData(auth, username);
    }
}
