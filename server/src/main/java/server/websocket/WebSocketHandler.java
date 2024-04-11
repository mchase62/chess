package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import spark.Spark;
import dataAccess.*;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

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
        System.out.println("Inside onmessage");

        UserGameCommand userCommand = new Gson().fromJson(message, UserGameCommand.class);
//        var conn = connections.getConnection(userCommand.getAuthString(), session);
//        if (conn != null) {
            switch (userCommand.getCommandType()) {
                case JOIN_PLAYER ->
                        joinPlayer(userCommand.getAuthString(), message, session, new Gson().fromJson(message, JoinPlayer.class));
                case JOIN_OBSERVER -> enter("", session);
                case MAKE_MOVE -> enter("", session);
                case LEAVE -> enter("", session);
                case RESIGN -> enter("", session);
            }
//        }
//        else {
//            System.out.println("Send error here");
//        }
    }

    private void joinPlayer(String auth, String message, Session session, JoinPlayer joinPlayer) throws IOException {
        ServerMessage serverMessage;
        Notification notification;
        String user;
//        connections.remove(auth);
        connections.add(auth, session, joinPlayer.getGameID());
        SQLAuthDAO sqlDAO = new SQLAuthDAO();
        try {
            user = sqlDAO.getUser(auth);
        }catch (Exception e) {
            throw new IOException();
        }
        System.out.println("This is the user " + user);
        serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "This is my notification");
        connections.broadcast(auth, notification, joinPlayer.getGameID());
    }


    private void enter(String userName, Session session) throws IOException {
        var message = String.format("%s is online", userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
//        connections.broadcast(userName, notification);
    }



    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s is offline", visitorName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
//        connections.broadcast(visitorName, notification);
    }
}
