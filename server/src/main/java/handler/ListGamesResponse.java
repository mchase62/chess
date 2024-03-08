package handler;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ListGamesResponse extends HandlerResponse{
    private Collection<GameItem> games;

    public ListGamesResponse(Collection<GameItem> games) {
        super(200);
        this.games = games;
    }

    public static class GameItem {
        private final int gameID;
        private final String whiteUsername;
        private final String blackUsername;
        private final String gameName;

        public int getGameID() {
            return gameID;
        }

        public String getWhiteUsername() {
            return whiteUsername;
        }

        public String getBlackUsername() {
            return blackUsername;
        }

        public String getGameName() {
            return gameName;
        }

        public GameItem(int gameID, String gameName, String whiteUsername, String blackUsername) {
            this.gameID = gameID;
            this.whiteUsername = whiteUsername;
            this.blackUsername = blackUsername;
            this.gameName = gameName;
        }

    }
}
