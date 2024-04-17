package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class Redraw extends UserGameCommand {
    private final int gameID;
    public Redraw(String auth, int gameID) {
        super(auth);
        setCommandType(CommandType.REDRAW);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
