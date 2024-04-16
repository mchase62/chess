package client.websocket;

//import webSocketMessages.Notification;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void serverMessage(ServerMessage serverMessage);

    // make chess board in notify function
}