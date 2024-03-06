package dataAccess;
import dataAccess.*;
import com.google.gson.Gson;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user (
            id int NOT NULL AUTO_INCREMENT,
            username varchar(256) NOT NULL,
            password varchar(256) NOT NULL,
            email varchar(256) NOT NULL,
            PRIMARY KEY (id)
            );
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }
    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM user";
            try (var ps = conn.prepareStatement(statement)) {
                ps.execute();
            }
        }catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
        if(getUser(user.username())!=null) { // the user already exists
            return "Fail";
        }
        var statement = "INSERT INTO user (username, password, email) values (?, ?, ?) ";

        // hash the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.password());

        var id = DatabaseManager.executeUpdate(statement, user.username(), hashedPassword, user.email());
        return "Success";
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) { // get user
                        return new UserData(rs.getString("username"),rs.getString("password"),rs.getString("email"));
                    }
                    else { // user doesn't exist
                        return null;
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
    }

    @Override
    public String getPassword(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
        return null;
    }

//    public void configureDatabase() throws DataAccessException {
//        DatabaseManager.createDatabase();
//
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        }
//        catch (SQLException ex) {
//            throw new DataAccessException("Unable to configure database");
//        }
//    }
//
//    private int executeUpdate(String statement, Object... params) throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
//
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException("unable to update database");
//        }
//    }
}
