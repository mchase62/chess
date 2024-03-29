package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import server.websocket.WebSocketHandler;
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
    private final WebSocketHandler webSocketHandler;
    public Server() {
        webSocketHandler = new WebSocketHandler();
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        System.out.println("Running on " + desiredPort);
        Spark.webSocket("/connect", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> new ClearHandler(clearService).handleClear(req, res));
        Spark.post("/user", (req, res) -> new UserHandler(userService).handleRegister(req, res));
        Spark.post("/session", (req, res) -> new UserHandler(userService).handleLogin(req, res));
        Spark.delete("/session", (req, res) -> new UserHandler(userService).handleLogout(req, res));
        Spark.post("/game", (req, res) -> new GameHandler(gameService).handleNewGame(req, res));
        Spark.get("/game", (req, res) -> new GameHandler(gameService).handleListGames(req, res));
        Spark.put("/game", (req, res) -> new GameHandler(gameService).handleJoinGame(req, res));
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
