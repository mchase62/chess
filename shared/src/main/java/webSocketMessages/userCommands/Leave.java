package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class Leave extends UserGameCommand {
    private final int gameID;
    public Leave(String auth, int gameID) {
        super(auth);
        setCommandType(CommandType.LEAVE);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
