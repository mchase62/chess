package dataAccess;

import model.AuthData;

import java.util.UUID;
import dataAccess.SQLUserDAO;
import dataAccess.DatabaseManager;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
            id int NOT NULL AUTO_INCREMENT,
            username varchar(256) NOT NULL,
            auth_token varchar(256) NOT NULL,
            PRIMARY KEY (id)
            );
            """
        };
        try {
            DatabaseManager.configureDatabase(createStatements);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth";
            try (var ps = conn.prepareStatement(statement)) {
                ps.execute();
            }
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    @Override
    public String getUser(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, auth_token FROM auth WHERE auth_token=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                    else {
                        return null;
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
    }
    @Override
    public String deleteAuth(String auth) throws DataAccessException {
        if (getUser(auth)==null)  // if the auth doesn't exist at the beginning
            return "fail";
        try (var conn = DatabaseManager.getConnection()) {
            var deleteStatement = "DELETE FROM auth WHERE auth_token=?";
            try (var ps = conn.prepareStatement(deleteStatement)) {
                ps.setString(1,auth);
                ps.execute();
            }
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        if (getUser(auth) == null) { // if the user is deleted
            return "success";
        }
        else {
            return "false";
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        SQLUserDAO sqlUserDAO = new SQLUserDAO();
        boolean userExists;
        // check if user exists in user table
        try (var conn = DatabaseManager.getConnection()) {
            var firstCheckStatement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(firstCheckStatement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) { // return null if the username doesn't exist in user
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        var statement = "INSERT INTO auth (username, auth_token) values (?, ?) ";
        DatabaseManager.executeUpdate(statement, username, auth);
        return new AuthData(auth,username);
    }
}
