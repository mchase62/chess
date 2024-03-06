package dataAccess;

import model.AuthData;

import java.util.UUID;
import dataAccess.SQLUserDAO;
import dataAccess.DatabaseManager;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
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
        DatabaseManager.configureDatabase(createStatements);
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
        try (var conn = DatabaseManager.getConnection()) {
            var delete_statement = "DELETE FROM auth WHERE auth_token=?";
            try (var ps = conn.prepareStatement(delete_statement)) {
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
            var first_check_statement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(first_check_statement)) {
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

        // check if user exists in auth table
        try (var conn = DatabaseManager.getConnection()) {
            var second_check_statement = "SELECT username FROM auth WHERE username=?";
            try (var ps = conn.prepareStatement(second_check_statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) { // user doesn't exist in auth
                        userExists = false;
                    }
                    else { // user exists in auth
                        userExists = true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        if(!userExists) { // the user doesn't exist in auth but does in user
            var statement = "INSERT INTO auth (username, auth_token) values (?, ?) ";
            DatabaseManager.executeUpdate(statement, username, auth);
            return new AuthData(auth,username);
        }
        else { // user exists in auth and user
            var statement = "UPDATE auth SET auth_token=? WHERE username=?";
            DatabaseManager.executeUpdate(statement, auth, username);
            return new AuthData(auth,username);
        }
    }
}
