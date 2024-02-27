package handler;

public class ErrorResponse extends HandlerResponse {
    private final String error;
    private final String message;

    public ErrorResponse(String error, String message) {
        super(500);
        this.error = error;
        this.message = message;
    }

    // Add getters for error and message
    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
