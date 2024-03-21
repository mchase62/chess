package client;

import server.ServerFacade;
import client.websocket.NotificationHandler;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }
}
