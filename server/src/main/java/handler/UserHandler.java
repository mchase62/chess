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
            String status = userService.createUser(user);
            if (status.equals("Fail")) { // user already exists
                response.status(403);
                return gson.toJson(new ErrorResponse("Error: already taken","Error: already taken"));
            }
            UserData createdUser = userService.getUser(user.username());
            AuthData auth = userService.createAuth(user.username());
            response.status(200); // code was successful
            return gson.toJson(new UserResponse(createdUser.username(), auth.authToken()));
        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleLogin(Request request, Response response) {
        try {
            UserData user = gson.fromJson(request.body(), UserData.class);
            AuthData auth = userService.login(user.username(), user.password());
            if (auth.authToken()==null) {
                response.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized", "Error: unauthorized"));
            }
            else {
                response.status(200); // code was successful
                return gson.toJson(new UserResponse(user.username(), auth.authToken()));
            }
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }

    public String handleLogout(Request request, Response response) {
        String auth = request.headers("authorization");
        String status;
        try {
            status = userService.logout(auth);
            if (status.equals("success")) {
                response.status(200);
                return "";
            }
            else {
                response.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized", "Error: unauthorized"));
            }
        }
        catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error registering user", e.getMessage()));
        }
    }
}
