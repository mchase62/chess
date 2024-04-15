package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import spark.Spark;
import dataAccess.*;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
//        var conn = connections.getConnection(userCommand.getAuthString(), session);
//        if (conn != null) {
            switch (userCommand.getCommandType()) {
                case JOIN_PLAYER ->
                        joinPlayer(userCommand.getAuthString(), message, session, new Gson().fromJson(message, JoinPlayer.class));
                case JOIN_OBSERVER -> joinObserver(userCommand.getAuthString(), message, session, new Gson().fromJson(message, JoinObserver.class));
                case MAKE_MOVE -> enter("", session);
                case LEAVE -> enter("", session);
                case RESIGN -> enter("", session);
            }
//        }
//        else {
//            System.out.println("Send error here");
//        }
    }

    public void makeMove() {

    }
    private String getGame(int gameID) throws IOException {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        try {
            return sqlGameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private String getUser(String auth) throws IOException {
        SQLAuthDAO sqlDAO = new SQLAuthDAO();
        try {
            return sqlDAO.getUser(auth);
        } catch (Exception e) {
            throw new IOException();
        }
    }
    private void joinObserver(String auth, String message, Session session, JoinObserver joinObserver) throws IOException {
        LoadGame loadGame;
        Notification notification;
        String game;
        ConcurrentHashMap<String, Connection> connectionMap;
        boolean valid = true;
        Error error;
        String user;

        int gameID = joinObserver.getGameID();
        try {
            game = getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
        connectionMap = connections.getConnections();
        for (var c : connectionMap.values()) {
            if (c.getPlayerColor() == null) {
                valid = false;
                error = new Error(ServerMessage.ServerMessageType.ERROR, "error: game doesn't exist");
                connections.add(auth, session, 0, null);
                connections.broadcastJoinObserve(auth, error, 0);
                break;
            }
        }

        if (valid) {
            try {
                user = getUser(auth);
            } catch (Exception e) {
                throw new IOException();
            }

            connections.add(auth, session, joinObserver.getGameID(), null);
            loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, user + " is observing the game. ");
            connections.broadcastJoinObserve(auth, notification, joinObserver.getGameID());
            connections.broadcastJoinObserve(auth, loadGame, joinObserver.getGameID());
        }
    }
    private void joinPlayer(String auth, String message, Session session, JoinPlayer joinPlayer) throws IOException {
        LoadGame loadGame;
        Notification notification;
        String user;
        String game;
        int gameID = joinPlayer.getGameID();
        ConcurrentHashMap<String, Connection> connectionMap;
        boolean valid = true;
        Error error;
        try {
            game = getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }

        connectionMap = connections.getConnections();
        for (var c : connectionMap.values()) {
            if (c.getPlayerColor() == null) {
                valid = false;
                error = new Error(ServerMessage.ServerMessageType.ERROR, "error: game doesn't exist");
                connections.add(auth, session, 0, null);
                connections.broadcastJoinObserve(auth, error, 0);
                break;
            }
            // if the player color is taken in that game
            else if(c.getPlayerColor().equals(joinPlayer.getPlayerColor().name()) && c.getGameID() == joinPlayer.getGameID()) {
                error = new Error(ServerMessage.ServerMessageType.ERROR, "error: already taken");
                connections.add(auth, session, 0, null);
                connections.broadcastJoinObserve(auth, error, 0);
                valid = false;
                break;
            }
        }

        if (valid) {
            SQLAuthDAO sqlDAO = new SQLAuthDAO();
            try {
                user = sqlDAO.getUser(auth);
            } catch (Exception e) {
                throw new IOException();
            }
            connections.add(auth, session, joinPlayer.getGameID(), joinPlayer.getPlayerColor().name());
            loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, user + " joined the game as " + joinPlayer.getPlayerColor().name());
            connections.broadcastJoinObserve(auth, notification, joinPlayer.getGameID());
            connections.broadcastJoinObserve(auth, loadGame, joinPlayer.getGameID());
        }
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
