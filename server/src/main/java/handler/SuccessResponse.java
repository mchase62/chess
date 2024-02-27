package handler;

public class SuccessResponse extends HandlerResponse {
    private final String username;
    private final String authToken;

    public SuccessResponse(String username, String authToken) {
        super(200);
        this.username = username;
        this.authToken = authToken;
    }

    // Add getters for username and authToken
    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}