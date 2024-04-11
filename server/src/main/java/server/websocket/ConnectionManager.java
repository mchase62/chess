package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String auth, Session session, int gameID) {
        var connection = new Connection(auth, session, gameID);
        connections.put(auth, connection);
    }

    public Connection getConnection(String auth, Session session) {
        return connections.get(auth);
    }
    public void remove(String auth) {
        connections.remove(auth);
    }

    public void broadcast(String excludeAuth, Notification notification, int gameID) throws IOException {
        System.out.println(notification.toString());
        System.out.println(excludeAuth);
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.auth.equals(excludeAuth)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.auth);
        }
    }
}