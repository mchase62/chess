package dataAccess;


import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private static MemoryUserDAO instance = null;
    private final Map<String, UserData> usersByUsername = new HashMap<>();

    public static synchronized MemoryUserDAO getInstance() {
        if(instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }
    @Override
    public void createUser(UserData user) {
        usersByUsername.put(user.username(), user); // put user in map
    }

    @Override
    public UserData getUser(String username) {
        return usersByUsername.get(username); // get user from map
    }

    @Override
    public void clear() {
        usersByUsername.clear(); // clear the map
    }

    @Override
    public String getPassword(String username) {
        if(usersByUsername.containsKey(username)) { // if the user exists
            return usersByUsername.get(username).password(); // return the password
        }
        else { // return null if user does not exist
            return null;
        }
    }
}
