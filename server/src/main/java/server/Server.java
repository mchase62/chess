package server;

import dataAccess.DataAccessException;
import spark.*;
import service.Service;
public class Server {
    private final Service service = new Service();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
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
        service.clear();
        return "";
    }

}
