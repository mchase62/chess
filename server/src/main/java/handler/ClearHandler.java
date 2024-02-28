package handler;
import dataAccess.DataAccessException;
import service.ClearService;
import spark.*;
import com.google.gson.Gson;

import com.google.gson.Gson;
public class ClearHandler {
    private ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public Object handleClear(Request req, Response res) {
        try {
            clearService.clearData();
            res.status(200);
            return "";
        }
         catch (
        DataAccessException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error Clearing", e.getMessage()));
        }
    }
}
