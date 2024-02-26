package server;

import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    UserService UserService = new UserService();

    public Object register(Request req, Response res) {
        UserService.register();
        res.status(200);
        return "{}";
    }
}
