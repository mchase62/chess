package webSocketMessages.serverMessages;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class LoadGame extends ServerMessage {
    ChessGame game;

    public LoadGame(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() { return game; }
    public String toString() {
        return new Gson().toJson(this);
    }
}
