package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Error extends ServerMessage{
    private String errorMessage;
    public Error(ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}