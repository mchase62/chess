package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String auth;
    public Session session;
    public int gameID;
    String playerColor;

    public Connection(String auth, Session session, int gameID, String playerColor) {
        this.auth = auth;
        this.session = session;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String getPlayerColor() {
        return playerColor;
    }
    public int getGameID() {
        return gameID;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}