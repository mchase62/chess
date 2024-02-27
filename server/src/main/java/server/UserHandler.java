package server;

import dataAccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;
import model.UserData;
public class UserHandler {
    UserService UserService = new UserService();

    public Object register(Request req, Response res) {
        try {
            UserService.register(new UserData(req.params(":user"), req.params(":password"), req.params(":email")));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        res.status(200);
        return "{}";
    }
}
