package webSocketMessages.userCommands;

import chess.ChessGame;
import model.GameData;

public class JoinObserver extends UserGameCommand {
    private int gameID;
    public JoinObserver(String auth, int gameID) {
        super(auth);
        setCommandType(CommandType.JOIN_OBSERVER);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
