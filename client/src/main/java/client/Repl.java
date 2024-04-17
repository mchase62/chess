package client;

import client.websocket.ServerMessageHandler;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import ui.ChessBoard;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.MakeMove;

public class Repl implements ServerMessageHandler {
    private final ChessClient client;
    private ChessBoard chessBoard;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_GREEN + WHITE_KING + " Welcome to 240 chess. Type Help to get started. " + WHITE_KING + "\n");
        System.out.println(SET_TEXT_COLOR_MAGENTA + client.help());
        Scanner scanner = new Scanner(System.in);
            // make server message observer interface with notify function

        var result = "";

        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            }
            catch (Throwable e) {
                System.out.println(e.toString());
            }
        }

    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLUE + ">>> ");
    }

    @Override
    public void serverMessage(ServerMessage serverMessage, String message) {
        if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
            String teamColor = client.getPlayerColor();
            chess.ChessBoard board = loadGame.getGame().getBoard();
            System.out.println();
            ChessBoard.drawBoard(teamColor, board);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            Notification notification = new Gson().fromJson(message, Notification.class);
            System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
            Error error = new Gson().fromJson(message, Error.class);
            System.out.println(SET_TEXT_COLOR_RED + error.getMessage());
        }
        printPrompt();
    }
}
