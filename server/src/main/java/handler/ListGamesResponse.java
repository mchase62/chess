package handler;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ListGamesResponse extends HandlerResponse{
    private Collection<game> games;

    public ListGamesResponse(Collection<game> games) {
        super(200);
        this.games = games;
    }

    public static class game {
        private final int gameID;
        private final String whiteUsername;
        private final String blackUsername;
        private final String gameName;
        public game(int gameID, String gameName, String whiteUsername, String blackUsername) {
            this.gameID = gameID;
            this.whiteUsername = whiteUsername;
            this.blackUsername = blackUsername;
            this.gameName = gameName;
        }

    }
}
