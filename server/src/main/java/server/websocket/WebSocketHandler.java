package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.ListGamesResponse;
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
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Collection;
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
        LoadGame loadGame;
        Notification notification;
        String user;
        String game;
        int gameID = joinPlayer.getGameID();
        ConcurrentHashMap<String, Connection> connectionMap;
        boolean valid = true;
        Error error;
//        connections.remove(auth);
        connectionMap = connections.getConnections();
        for (var c : connectionMap.values()) {
            // if the player color is taken in that game
            if(c.getPlayerColor().equals(joinPlayer.getPlayerColor().name()) && c.getGameID() == joinPlayer.getGameID()) {
                error = new Error(ServerMessage.ServerMessageType.ERROR, "error: already taken");
                connections.broadcast(auth, error, joinPlayer.getGameID());
                valid = false;
                break;
            }
        }



        if (valid) {
            connections.add(auth, session, joinPlayer.getGameID(), joinPlayer.getPlayerColor().name());
            SQLAuthDAO sqlDAO = new SQLAuthDAO();
            try {
                user = sqlDAO.getUser(auth);
            } catch (Exception e) {
                throw new IOException();
            }
            SQLGameDAO sqlGameDAO = new SQLGameDAO();
            try {
                game = sqlGameDAO.getGame(gameID);
            } catch (Exception e) {
                throw new IOException();
            }
            loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "This is my notification");
            connections.broadcast(auth, notification, joinPlayer.getGameID());
            connections.broadcast(auth, loadGame, joinPlayer.getGameID());
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
