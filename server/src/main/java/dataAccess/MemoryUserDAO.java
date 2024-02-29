package dataAccess;


import model.UserData;

import javax.xml.crypto.Data;
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
    public String createUser(UserData user) {
        if(usersByUsername.containsKey(user.username())) { // user already exists
            return "Fail";
        }
        usersByUsername.put(user.username(), user); // put user in map
        return "Success";
    }

    @Override
    public UserData getUser(String username) {
        if(usersByUsername.containsKey(username)) { // if user exists
            return usersByUsername.get(username); // get user from map
        }
        return null; // return null if user doesn't exist
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
