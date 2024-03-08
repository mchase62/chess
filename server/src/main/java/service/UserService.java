package service;
import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
    }

    public String createUser(UserData user) throws DataAccessException {
        return userDAO.createUser(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        return authDAO.createAuth(username);
    }

    public AuthData login(String username, String password) throws DataAccessException {
        String hashedPassword = userDAO.getPassword(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, hashedPassword)) {
            return authDAO.createAuth(username);
        }
        return new AuthData(null, null);
    }

    public String logout(String auth) throws DataAccessException {
        return authDAO.deleteAuth(auth);
    }
}
