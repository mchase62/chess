package dataAccess;

import model.AuthData;

import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{


    @Override
    public void clear() throws DataAccessException {

    }
    @Override
    public String getUser(String auth) throws DataAccessException {
        return "";
    }
    @Override
    public String deleteAuth(String auth) throws DataAccessException {
//        if(usersByAuth.containsKey(auth)) {
//            usersByAuth.remove(auth);
//            return "success";
//        }
//        else {
//            return "fail";
//        }
        return "";
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
//        String auth = UUID.randomUUID().toString();
//        if(MemoryUserDAO.getInstance().getUser(username)==null) { // user doesn't exist
//            return null;
//        }
//        usersByAuth.put(auth, username);
//        return new AuthData(auth, username);
        return null;
    }
}
