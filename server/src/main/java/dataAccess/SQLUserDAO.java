package dataAccess;

import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;

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
        return null;
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            'id' int NOT NULL AUTO_INCREMENT,
            'username' varchar(256) NOT NULL,
            'password' varchar(256) NOT NULL,
            'email' varchar(256) NOT NULL,
            PRIMARY KEY ('id')
            )
            """
    };

    public void configureDatabase() throws DataAccessException {
        System.out.println("Configuring");
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }

//    private int executeUpdate(String statement, Object... params) throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, null);
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
