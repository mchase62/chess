package client;

import chess.ChessGame;
import client.websocket.WebSocketFacade;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import client.websocket.NotificationHandler;
import java.util.Arrays;
import exception.ResponseException;

public class ChessClient {
    private String userName = null;
    private String password = null;
    private String auth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;
    private WebSocketFacade ws;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new Repl(serverUrl).run();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> logIn(params);
                case "register" -> register(params);
                case "logout" -> logOut();
                case "create" -> createGame(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            userName = params[0];
            password = params[1];
            String email = params[2];
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterChessServer(userName);
            UserData newUser = new UserData(userName, password, email);
            server.register(newUser);
            server.login(newUser);
            state = State.SIGNEDIN;
            return String.format("You registered as %s.", userName);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String logIn(String... params) throws ResponseException {
        if (params.length >= 2) {
            userName = params[0];
            password = params[1];
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterChessServer(userName);
            UserData user = new UserData(userName, password,"");
            auth = server.login(user).authToken();
            state = State.SIGNEDIN;
            return String.format("You logged in as %s.", userName);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String logOut() throws ResponseException {
        assertSignedIn();
        ws.leaveChessServer(userName);
        ws = null;
        state = State.SIGNEDOUT;
        return String.format("%s logged out.", userName);
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            ChessGame chessGame = new ChessGame();
            GameData game = new GameData(0,null,null,gameName, chessGame);
            server.createGame(game, auth);
            return String.format("%s created.", gameName);
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }
    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK]<empty>] - a game
                observer <ID> - a game
                logout - when you are done
                quit - playing chess
                help with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT)
            throw new ResponseException(400, "You must sign in");
    }
}
