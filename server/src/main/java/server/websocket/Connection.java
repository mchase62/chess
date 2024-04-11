package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String auth;
    public Session session;
    public int gameID;

    public Connection(String auth, Session session, int gameID) {
        this.auth = auth;
        this.session = session;
        this.gameID = gameID;
    }

    public int getGameID(String auth) {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}