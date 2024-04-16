package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class Resign extends UserGameCommand {
    private final int gameID;
    public Resign(String auth, int gameID, ChessMove move) {
        super(auth);
        setCommandType(CommandType.LEAVE);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
