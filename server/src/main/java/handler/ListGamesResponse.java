package handler;

import model.GameData;

import java.util.Map;

public class ListGamesResponse extends HandlerResponse{
    private Map<Integer, GameData> gamesMap;

    public ListGamesResponse(Map<Integer, GameData> gamesMap) {
        super(200);
        this.gamesMap = gamesMap;
    }
}
