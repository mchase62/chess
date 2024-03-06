package dataAccess;

import com.google.gson.Gson;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
//        usersByUsername.clear()
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
//        if(usersByUsername.containsKey(user.username())) { // user already exists
//            return "Fail";
//        }
//        usersByUsername.put(user.username(), user); // put user in map
//        return "Success";
        var statement = "INSERT INTO user (username, password, email) values (?, ?, ?) ";
//
        // hash the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.password());

        var id = executeUpdate(statement, user.username(), hashedPassword, user.email());
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
//        if(usersByUsername.containsKey(username)) { // if user exists
//            return usersByUsername.get(username); // get user from map
//        }
//        return null; // return null if user doesn't exist
        return null;
    }

    @Override
    public String getPassword(String username) throws DataAccessException {
//        if(usersByUsername.containsKey(username)) { // if the user exists
//            return usersByUsername.get(username).password(); // return the password
//        }
//        else { // return null if user does not exist
//            return null;
//        }
        // make password 60
        return null;
    }
    private final String[] createStatements = {
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

    public void configureDatabase() throws DataAccessException {
        System.out.println("Configuring");
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("BOP");
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    System.out.println("BOP!");
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        System.out.println("Executing");
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("DSFLK");
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                System.out.println(statement);
                for (var i = 0; i < params.length; i++) {
                    System.out.println("In the loop");
                    var param = params[i];
                    System.out.println(param);
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                System.out.println("GG");
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DataAccessException("unable to update database");
        }
    }
}
