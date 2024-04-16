package webSocketMessages.serverMessages;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class LoadGame extends ServerMessage {
    ChessGame game;

    public LoadGame(ServerMessageType type, String gameJson) {
        super(type);
        GameData gameData = new Gson().fromJson(gameJson, GameData.class);
        this.game = gameData.game();
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        drawTicTacToeBoard(PrintStream out, String teamColor, chess.ChessBoard board)
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
