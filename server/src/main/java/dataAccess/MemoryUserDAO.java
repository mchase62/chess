package dataAccess;


import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private final Map<String, UserData> usersByUsername = new HashMap<>();

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
