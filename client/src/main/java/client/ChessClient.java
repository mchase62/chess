package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import client.websocket.NotificationHandler;
import java.util.Arrays;
import exception.ResponseException;

public class ChessClient {
    private String gameID = null;
    private String userName = null;
    private String password = null;
    private String auth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;
    private GameState gameState = GameState.OUTGAME;
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

//            ws.enterChessServer(userName);
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
            UserData user = new UserData(userName, password,"");
            auth = server.login(user).authToken();
            ws.enterChessServer(auth);
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
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        String playerColor;
        ChessGame.TeamColor color = null;
        assertSignedIn();
        if (params.length >= 1) {
            gameID = params[0];
            playerColor = (params.length >= 2) ? params[1] : null;
            if (playerColor!=null) { // if the player color is not null
                playerColor = playerColor.toUpperCase();
                if (playerColor.equals("WHITE"))
                    color = ChessGame.TeamColor.WHITE;
                else
                    color = ChessGame.TeamColor.BLACK;
            }

            server.joinGame(playerColor, Integer.parseInt(gameID), auth); // this is where the join game request is made
            ws.joinPlayer(auth, Integer.parseInt(gameID), color);
            gameState = GameState.INGAME;
            return String.format("Joined Game " + gameID + " as " + playerColor);
        }
        throw new ResponseException(400, "Expected: join <ID> [WHITE|BLACK]<empty>]");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            String gameID = params[0];
            server.joinGame(null, Integer.parseInt(gameID), auth);
            ws.joinObserver(auth, Integer.parseInt(gameID));
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
            list += (games[i].gameID()) + ". " + formattedGameName + "  White: " + formattedWhiteUsername + "  Black: " + formattedBlackUsername + "\n";
        }
        return list;
    }

    public ChessPosition getPosition(String input) throws ResponseException {
        boolean isValidRow = false;
        boolean isValidCol = false;
        char[] validCol = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char[] validRow = {'1', '2', '3', '4', '5', '6', '7', '8'};
        for (char valid : validRow) {
            if (valid == input.charAt(1)) {
                isValidRow = true;
                break;
            }
        }
        for (char valid : validCol) {
            if (valid == input.charAt(0)) {
                isValidCol = true;
                break;
            }
        }

        if(!isValidRow || !isValidCol) { // if the move input isn't what we expected
            throw new ResponseException(400, "Expected: <Letter><Number> <Letter><Number> <Promotion Piece>");
        }

        int col = 0;
        int row;
        row = Integer.parseInt(String.valueOf(input.charAt(1)));
        switch (input.charAt(0)) {
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
        }
        return new ChessPosition(row, col);
    }
    public String makeMove(String... params) throws ResponseException {
        assertSignedIn();
        assertInGame();
        String startInput;
        String endInput;
        ChessPiece.PieceType promotionPiece = null;
        String promotion;
        ChessPosition start;
        ChessPosition end;
        if(params.length >= 2) {
            startInput = params[0];
            endInput = params[1];

            start = getPosition(startInput);
            end = getPosition(endInput);

            if (params.length == 3) { // if a promotion piece is included
                promotion = params[2].toLowerCase();
                switch (promotion) {
                    case "queen" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                    case "rook" -> promotionPiece = ChessPiece.PieceType.ROOK;
                    case "knight" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                    case "bishop" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                }
            }
        }
        else // if there wasn't enough parameters
            throw new ResponseException(400, "Expected: <Letter><Number> <Letter><Number> <Promotion Piece>");

        ChessMove move = new ChessMove(start, end, promotionPiece);
        ws.makeMove(auth, Integer.parseInt(gameID), move);
        throw new ResponseException(400, "Expected: <Letter><Number> <Letter><Number> <Promotion Piece>");
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
        else if (gameState == GameState.INGAME) {
            return """
                    redraw - redraws chessboard
                    leave - leaves current game
                    make move <Letter><Number> <Letter><Number> <Promotion Piece>
                    resign - resign from current game
                    highlight moves <Letter><Number> - highlights all legal moves for position selected
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
    private void assertInGame() throws ResponseException {
        if (gameState == GameState.OUTGAME) {
            throw new ResponseException(400, "You must enter a game");
        }
    }
}
