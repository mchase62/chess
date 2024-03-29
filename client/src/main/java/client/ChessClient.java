package client;

import chess.ChessGame;
import client.websocket.WebSocketFacade;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import client.websocket.NotificationHandler;
import java.util.Arrays;
import exception.ResponseException;
import ui.TicTacToe;

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
                case "observe" -> observeGame(params);
                case "join" -> joinGame(params);
                case "list" -> listGames();
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
            auth = server.login(newUser).authToken();
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
        server.logout(auth);
        ws.leaveChessServer(userName);
        ws = null;
        state = State.SIGNEDOUT;
        return String.format("%s logged out.", userName);
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            String gameName = params[0];
            ChessGame chessGame = new ChessGame();
            GameData game = new GameData(0,null,null,gameName, chessGame);
            server.createGame(game, auth);
            return String.format("%s created.", gameName);
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        String playerColor = null;
        GameData game;
        assertSignedIn();
        if (params.length >= 1) {
            String gameID = params[0];
            playerColor = (params.length >= 2) ? params[1] : null;
            playerColor = playerColor.toUpperCase();
            server.joinGame(playerColor, Integer.parseInt(gameID), auth);
            return String.format("Joined Game " + gameID + " as " + playerColor);
        }
        throw new ResponseException(400, "Expected: join <ID> [WHITE|BLACK]<empty>]");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            String gameID = params[0];
            server.joinGame(null, Integer.parseInt(gameID), auth);
            return String.format("Observing Game " + gameID);
        }
        throw new ResponseException(400, "Expected: observe <ID>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        GameData[] games = server.listGames(auth);
        String list = "";
        for (int i = 0; i < games.length; i++) {
            String formattedGameName = String.format("%-20s", games[i].gameName()); // Adjust the padding length as needed
            String formattedWhiteUsername = String.format("%-20s", games[i].whiteUsername()); // Adjust the padding length as needed
            String formattedBlackUsername = String.format("%-20s", games[i].blackUsername()); // Adjust the padding length as needed
            list += (i + 1) + ". " + formattedGameName + "  White: " + formattedWhiteUsername + "  Black: " + formattedBlackUsername + "\n";
        }
        return list;
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
                observe <ID> - a game
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
