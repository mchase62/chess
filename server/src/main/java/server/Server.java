package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import service.UserService;
import spark.*;
import service.ClearService;
import model.UserData;

import java.util.ArrayList;

public class Server {
    private final ClearService clearService = new ClearService();
    private final UserService userService = new UserService();
    private ArrayList<UserData> users = new ArrayList<>();
    public Server() {
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> new ClearHandler().clear(req, res));
        Spark.post("/user", (req, res) -> new UserHandler().register(req, res));
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int port() {
        return Spark.port();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.clearData();
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        user = userService.register(user);
        users.add(user);
        return new Gson().toJson(user);
    }

}
