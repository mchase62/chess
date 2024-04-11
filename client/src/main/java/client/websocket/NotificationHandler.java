package client.websocket;

//import webSocketMessages.Notification;

import webSocketMessages.serverMessages.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}