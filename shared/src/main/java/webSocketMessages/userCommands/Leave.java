package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class Leave extends UserGameCommand {
    private final int gameID;
    public Leave(String auth, int gameID, ChessMove move) {
        super(auth);
        setCommandType(CommandType.LEAVE);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
