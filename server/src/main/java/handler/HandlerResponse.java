package handler;

public class HandlerResponse {
    protected int status;

    public HandlerResponse(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    // Add getters for status
}