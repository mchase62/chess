package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

////need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.serverMessage(serverMessage, message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void makeMove(String auth, int gameID, ChessMove move) throws ResponseException {
        try {
            MakeMove gameCommand = new MakeMove(auth, gameID, move);
            gameCommand.setCommandType(UserGameCommand.CommandType.MAKE_MOVE);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        }  catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void joinPlayer(String auth, int gameID, ChessGame.TeamColor playerColor) throws ResponseException {
        try {
            JoinPlayer gameCommand = new JoinPlayer(auth, gameID, playerColor);
            gameCommand.setCommandType(UserGameCommand.CommandType.JOIN_PLAYER);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        }  catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String auth, int gameID) throws ResponseException {
        try {
            Leave leaveCommand = new Leave(auth, gameID);
            leaveCommand.setCommandType(UserGameCommand.CommandType.LEAVE);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinObserver(String auth, int gameID) throws ResponseException {
        try {
            JoinObserver gameCommand = new JoinObserver(auth, gameID);
            gameCommand.setCommandType(UserGameCommand.CommandType.JOIN_OBSERVER);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        }  catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void enterChessServer(String auth) throws ResponseException {
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            this.session.getBasicRemote().sendText(new Gson().toJson(serverMessage));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveChessServer(String userName) throws ResponseException {
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            this.session.getBasicRemote().sendText(new Gson().toJson(serverMessage));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}