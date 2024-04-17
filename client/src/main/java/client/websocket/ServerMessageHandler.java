package client.websocket;

//import webSocketMessages.Notification;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void serverMessage(ServerMessage serverMessage, String message);

    // make chess board in notify function
}