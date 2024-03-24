package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import server.ServerFacade;
import spark.Spark;
import webSocketMessages.Action;
import dataAccess.*;
import webSocketMessages.Notification;

import java.io.IOException;

import static java.lang.System.exit;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/connect", Server.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        System.out.println("In websocket");
        System.out.println(action.type());
        switch (action.type()) {
            case REGISTER -> register(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
        }
    }

    private void register(String visitorName, Session session) throws IOException {
        System.out.println("In websocket");
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }
}
