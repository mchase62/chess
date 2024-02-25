package dataAccess;

import model.UserData;

import java.util.HashMap;

public class UserDataAccess implements DataAccess{
    final private HashMap<Integer, UserData> users = new HashMap<>();
    public void clear() {
        users.clear();
    }
}
