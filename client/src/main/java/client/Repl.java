package client;

import client.websocket.NotificationHandler;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_GREEN + WHITE_KING + " Welcome to 240 chess. Type Help to get started. " + WHITE_KING);
        Scanner scanner = new Scanner(System.in);

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
}
