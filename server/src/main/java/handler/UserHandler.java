package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import model.UserData;
public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public String handleRegister(Request request, Response response) {
        try {
            UserData user = gson.fromJson(request.body(), UserData.class);
            if(user.username() == null || user.password() == null || user.email() == null) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request","Error: bad request"));
            }
            if (userService.getUser(user.username()) != null) {
                response.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken","Error: already taken"));
            }
            userService.createUser(user);
            UserData createdUser = userService.getUser(user.username());
            AuthData auth = userService.createAuth(user.username());
            response.status(200); // code was successful
            return gson.toJson(new SuccessResponse(createdUser.username(), auth.authToken()));
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleLogin(Request request, Response response) {
        try {
            UserData user = gson.fromJson(request.body(), UserData.class);
            AuthData auth = userService.login(user.username(), user.password());
            if (auth == null) {
                response.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized", "Error: unauthorized"));
            }
            else {
                response.status(200); // code was successful
                return gson.toJson(new SuccessResponse(user.username(), auth.authToken()));
            }
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }
}
