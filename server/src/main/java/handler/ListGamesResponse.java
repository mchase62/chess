package handler;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ListGamesResponse extends HandlerResponse{
    private Collection<ArrayList<Object>> games;

    public ListGamesResponse(Collection<ArrayList<Object>> games) {
        super(200);
        this.games = games;
    }
}
