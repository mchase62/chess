package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class MakeMove extends UserGameCommand {
    private int gameID;
    private ChessMove move;
    public MakeMove(String auth, int gameID, ChessMove move) {
        super(auth);
        setCommandType(CommandType.MAKE_MOVE);
        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
