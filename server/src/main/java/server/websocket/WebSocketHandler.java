package server.websocket;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.GameData;
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
import webSocketMessages.userCommands.*;


import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        switch (userCommand.getCommandType()) {
            case JOIN_PLAYER ->
                    joinPlayer(userCommand.getAuthString(), message, session, new Gson().fromJson(message, JoinPlayer.class));
            case JOIN_OBSERVER -> joinObserver(userCommand.getAuthString(), message, session, new Gson().fromJson(message, JoinObserver.class));
            case MAKE_MOVE -> makeMove(userCommand.getAuthString(), message, session, new Gson().fromJson(message, MakeMove.class));
            case LEAVE -> leave(userCommand.getAuthString(), message, session, new Gson().fromJson(message, Leave.class));
            case RESIGN -> resign(userCommand.getAuthString(), message, session, new Gson().fromJson(message, Resign.class));
        }
    }

    public void resign(String auth, String message, Session session, Resign resign) throws IOException {

    }
    public void leave(String auth, String message, Session session, Leave leave) throws IOException {

    }
    public void makeMove(String auth, String message, Session session, MakeMove move) throws IOException {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        int gameID = move.getGameID();
        ChessMove chessMove;
        ChessGame chessGame;
        ChessPosition start;
        Notification statusNotification = null;
        boolean valid = false;
        String game;
        try {
            game = sqlGameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
        GameData gameData = new Gson().fromJson(game, GameData.class);
        chessGame = gameData.game();
        chessMove = move.getMove();
        start = chessMove.getStartPosition();
        if (chessGame.getBoard()==null) { // if the board is empty
            ChessBoard newBoard = new ChessBoard();
            newBoard.resetBoard();
            chessGame.setBoard(newBoard);
        }
        if (connections.getConnection(auth, session) == null) { // an observer
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "error: observer cannot make move");
            Connection newConnection = new Connection(auth, session, gameID, null);
            newConnection.send(error.toString()); // send to current as well
            return;
        }
        else if(!chessGame.getTeamTurn().toString().equals(connections.getConnection(auth, session).getPlayerColor())) { // check if our turn
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "error: not player's turn");
            Connection newConnection = new Connection(auth, session, gameID, null);
            newConnection.send(error.toString()); // send to current as well
            return;
        }
        for (ChessMove validMove : chessGame.validMoves(start)) {
            if(validMove.equals(chessMove)) {
                valid = true;
                break;
            }
        }
        if (valid) {
            try {
                chessGame.makeMove(chessMove);
                if(gameOver(chessGame).equals("WHITE")) {
                    statusNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "White is in checkmate");
                }
                else if(gameOver(chessGame).equals("BLACK")) {
                    statusNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in checkmate");
                }
                else if(inCheck(chessGame).equals("WHITE")) {
                    statusNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "White is in check");
                }
                else if(inCheck(chessGame).equals("BLACK")) {
                    statusNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in check");
                }
                System.out.println("CC");
                if (statusNotification != null) {
                    Connection newConnection = new Connection(auth, session, gameID, null);
                    newConnection.send(statusNotification.toString()); // send to current as well
                    connections.broadcastMakeMove(auth, statusNotification, gameID);
                }
            }
            catch (Exception e) {
                throw new IOException();
            }
            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, chessGame.getBoard().getPiece(chessMove.getEndPosition()).getTeamColor().toString() + " made move: " + chessMove.toString());
            connections.broadcastMakeMove(auth, notification, gameID);
            connections.broadcastMakeMove(auth, loadGame, gameID);
        }
        else { // not a valid move
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "error: not a valid move");
            Connection newConnection = new Connection(auth, session, gameID, null);
            newConnection.send(error.toString());
        }
    }

    private String getGame(int gameID) throws IOException {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        try {
            return sqlGameDAO.getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private String gameOver(ChessGame game) {
        if(game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return "WHITE";
        }
        else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return "BLACK";
        }
        else if(game.isInStalemate(ChessGame.TeamColor.BLACK) || game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            return "DRAW";
        }
        else
            return "NO";
    }

    private String inCheck(ChessGame game) {
        if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            return "WHITE";
        }
        else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            return "BLACK";
        }
        else
            return "NO";
    }

    private String[] getUsers(int gameID) throws IOException {
        SQLGameDAO sqlGameDAO = new SQLGameDAO();
        try {
            return sqlGameDAO.getUsers(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private String getUser(String auth) throws IOException {
        SQLAuthDAO sqlDAO = new SQLAuthDAO();
        try {
            return sqlDAO.getUser(auth);
        } catch (Exception e) {
            throw new IOException();
        }
    }
    private void joinObserver(String auth, String message, Session session, JoinObserver joinObserver) throws IOException {
        LoadGame loadGame;
        Notification notification;
        String game;
        ConcurrentHashMap<String, Connection> connectionMap;
        boolean valid = false;
        Error error;
        String user;

        int gameID = joinObserver.getGameID();
        try {
            game = getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }

        connectionMap = connections.getConnections();
        // go through map and check if a connection has the game id
        for (var c : connectionMap.values()) {
            if (gameID == c.getGameID()) { // game exists
                valid = true;
                break;
            }
        }
        try {
            user = getUserAuth(auth);
        } catch (Exception e) {
            throw new IOException();
        }
        if(user==null) {
            error = new Error(ServerMessage.ServerMessageType.ERROR, "error: invalid auth token");
            Connection errorConnection = new Connection(auth, session, 0, null);
            errorConnection.send(error.toString());
            return;
        }
        if (valid) {
            try {
                user = getUser(auth);
            } catch (Exception e) {
                throw new IOException();
            }
            Connection observeConnection = new Connection(auth, session, joinObserver.getGameID(), null);
            connections.add(auth, session, joinObserver.getGameID(), null);

            loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, user + " is observing the game. ");
            connections.broadcastJoinObserve(auth, notification, joinObserver.getGameID());
            observeConnection.send(loadGame.toString());
        }
        else {
            error = new Error(ServerMessage.ServerMessageType.ERROR, "error: game doesn't exist");
            // change to send from connection class
            Connection errorConnection = new Connection(auth, session, 0, null);
            errorConnection.send(error.toString());
        }
    }
    private void joinPlayer(String auth, String message, Session session, JoinPlayer joinPlayer) throws IOException {
        LoadGame loadGame;
        Notification notification;
        String[] users;
        String user;
        String game;
        int gameID = joinPlayer.getGameID();
        ConcurrentHashMap<String, Connection> connectionMap;
        boolean valid = false;
        Error error;
        try {
            game = getGame(gameID);
        } catch (Exception e) {
            throw new IOException();
        }
        if (!game.equals("bad request")) {
            valid = true;
        }
        try {
            users = getUsers(gameID);
        } catch (Exception e) {
            throw new IOException();
        }

        if(users.length > 1) {
            if (users[0] == null && users[1] == null) {
                error = new Error(ServerMessage.ServerMessageType.ERROR, "error: empty team");
                Connection errorConnection = new Connection(auth, session, 0, null);
                errorConnection.send(error.toString());
                return;
            }
        }
        connectionMap = connections.getConnections();
        for (var c : connectionMap.values()) { // if there are already other connections
            if(c.getGameID() == joinPlayer.getGameID() && c.getPlayerColor()!=null) { // this is not an observer
                // if the player color is taken in that game
                if (c.getPlayerColor().equals(joinPlayer.getPlayerColor().name()) && c.getGameID() == joinPlayer.getGameID()) {
                    error = new Error(ServerMessage.ServerMessageType.ERROR, "error: already taken");
                    Connection errorConnection = new Connection(auth, session, 0, null);
                    errorConnection.send(error.toString());
                    return;
                }
            }
        }

        try {
            user = getUserAuth(auth);
        } catch (Exception e) {
            throw new IOException();
        }
        if(user==null) {
            error = new Error(ServerMessage.ServerMessageType.ERROR, "error: invalid auth token");
            Connection errorConnection = new Connection(auth, session, 0, null);
            errorConnection.send(error.toString());
            return;
        }
        if (valid) {
            connections.add(auth, session, joinPlayer.getGameID(), joinPlayer.getPlayerColor().name());
            
            loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, user + " joined the game as " + joinPlayer.getPlayerColor().name());
            connections.broadcastJoinObserve(auth, notification, joinPlayer.getGameID());
            connections.broadcastJoinObserve(auth, loadGame, joinPlayer.getGameID());
        }
        else {
            error = new Error(ServerMessage.ServerMessageType.ERROR, "error: game doesn't exist");
            Connection errorConnection = new Connection(auth, session, 0, null);
            errorConnection.send(error.toString());
        }
    }

    private static String getUserAuth(String auth) throws IOException {
        SQLAuthDAO sqlDAO = new SQLAuthDAO();
        try {
            return sqlDAO.getUser(auth);
        } catch (Exception e) {
            throw new IOException();
        }
    }
}
