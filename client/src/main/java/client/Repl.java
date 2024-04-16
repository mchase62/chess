package client;

import client.websocket.ServerMessageHandler;
import webSocketMessages.serverMessages.Notification;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import ui.ChessBoard;
import webSocketMessages.serverMessages.ServerMessage;

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
    public void serverMessage(ServerMessage serverMessage) {
        System.out.println(SET_TEXT_COLOR_RED + serverMessage.toString());

        printPrompt();
    }
}
