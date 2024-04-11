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
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

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
        UserGameCommand userCommand = new Gson().fromJson(message, UserGameCommand.class);

        switch (userCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userCommand.getAuthString(), message, session);
            case JOIN_OBSERVER -> enter("", session);
            case MAKE_MOVE -> enter("", session);
            case LEAVE -> enter("", session);
            case RESIGN -> enter("", session);
        }
    }

    private void joinPlayer(String auth, String message, Session session) throws IOException {
        connections.add(auth, session);
        System.out.println("Inside join player");

    }


    private void enter(String userName, Session session) throws IOException {


        var message = String.format("%s is online", userName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(userName, notification);
    }



    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s is offline", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }
}
