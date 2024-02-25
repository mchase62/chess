package server;
import service.ClearService;
import spark.*;
import com.google.gson.Gson;
public class ClearHandler {
    ClearService clearService = new ClearService();

    public Object clear(Request req, Response res) {
        clearService.clearData();
        res.status(200);
        return "{}";
    }
}
