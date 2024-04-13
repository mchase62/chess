package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.Error;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String auth, Session session, int gameID, String playerColor) {
        var connection = new Connection(auth, session, gameID, playerColor);
        connections.put(auth, connection);
    }

    public ConcurrentHashMap<String, Connection> getConnections() {
        return connections;
    }
    public Connection getConnection(String auth, Session session) {
        return connections.get(auth);
    }
    public void remove(String auth) {
        connections.remove(auth);
    }

    public void broadcast(String excludeAuth, ServerMessage serverMessage, int gameID) throws IOException {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> serverMessage = new Gson().fromJson(serverMessage.toString(), Notification.class);
            case LOAD_GAME -> serverMessage = new Gson().fromJson(serverMessage.toString(), LoadGame.class);
            case ERROR -> serverMessage = new Gson().fromJson(serverMessage.toString(), Error.class);
        }

        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR && c.auth.equals(excludeAuth)) { // if this is an error message
                    c.send(serverMessage.toString());
                }
                if (c.gameID == gameID) { // if it's the same game
                    if (!c.auth.equals(excludeAuth) && serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) { // if not the current player
                        c.send(serverMessage.toString());
                    }
                    if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME && c.auth.equals(excludeAuth)) { // if we're loading the game
                        c.send(serverMessage.toString());
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
//        for (var c : removeList) {
//            connections.remove(c.auth);
//        }
    }
}