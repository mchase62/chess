package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.GameService;
import service.UserService;
import spark.*;
import service.ClearService;
import model.UserData;

import java.util.ArrayList;

public class Server {
    private final ClearService clearService = new ClearService();
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    public Server() {
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> new ClearHandler(clearService).handleClear(req, res));
        Spark.post("/user", (req, res) -> new UserHandler(userService).handleRegister(req, res));
        Spark.post("/session", (req, res) -> new UserHandler(userService).handleLogin(req, res));
        Spark.delete("/session", (req, res) -> new UserHandler(userService).handleLogout(req, res));
        Spark.post("/game", (req, res) -> new GameHandler(gameService).handleNewGame(req, res));
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
        userService.createUser(user);
        return new Gson().toJson(user);
    }

}
