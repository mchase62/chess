package handler;

public class CreateGameResponse extends HandlerResponse {
    private final int gameID;

    public CreateGameResponse(int gameID) {
        super(200);
        this.gameID = gameID;
    }
    public int getGameID() {
        return gameID;
    }

}