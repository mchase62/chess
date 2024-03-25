package clientTests;

import chess.ChessGame;
import client.ChessClient;
import client.websocket.NotificationHandler;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import webSocketMessages.Notification;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    public static ChessClient client;
    public static ServerFacade sf;
//    private final NotificationHandler notificationHandler;
    public class NotificationHandlerImpl implements NotificationHandler {
        @Override
        public void notify(Notification notification) {

        }

    }
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        sf = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        authDAO = new SQLAuthDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

    }
    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            UserData returnedUser = sf.register(user);
            assertEquals("my_username", returnedUser.username());
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testRegisterFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            UserData firstUser = sf.register(user);
            UserData secondUser = sf.register(user);
            fail("Registered with same username");
        } catch (ResponseException e) { // catch error when registering with same username
            System.out.println(e.toString());
        }
    }

    @Test
    public void testLoginSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            assertNotNull(authData);
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testLoginFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            UserData wrongPassword = new UserData("my_username", "not_a_password", "my_email");
            AuthData authData = sf.login(wrongPassword);
            fail("Logged in with wrong password");
        } catch (ResponseException e) { // catch error when logging in with wrong password
            System.out.println(e.toString());
        }
    }

    @Test
    public void testLogoutSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.logout(authData.authToken());

            // attempt to create a game while logged out
            sf.createGame(new GameData(1, null,null,"game_name",new ChessGame()), authData.authToken());
            fail("Created a game while logged out");
        } catch (ResponseException e) { // catch error when thrown because user logged out cannot create game
            System.out.println(e.toString());
        }
    }

    @Test
    public void testLogoutFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.logout("not_an_auth_token");
            fail("Logged out with invalid auth token");
        } catch (ResponseException e) { // throw an error when logging out with wrong auth token
            System.out.println(e.toString());
        }
    }

    @Test
    public void testCreateGameSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            GameData game = sf.createGame(new GameData(1, null,null,"my_game",new ChessGame()), authData.authToken());
            assertNotNull(game);
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testCreateGameFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.logout(authData.authToken());

            // attempt to create a game while logged out
            sf.createGame(new GameData(1, null,null,"game_name",new ChessGame()), authData.authToken());
            fail("Created a game while logged out");
        } catch (ResponseException e) { // catch error when thrown because user logged out cannot create game
            System.out.println(e.toString());
        }
    }

    @Test
    public void testJoinGameSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.createGame(new GameData(1, null,null,"my_game",new ChessGame()), authData.authToken());
            sf.joinGame("WHITE", 1, authData.authToken());
            assertEquals(sf.listGames(authData.authToken())[0].whiteUsername(), user.username());
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testJoinGameFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.createGame(new GameData(1, null,null,"my_game",new ChessGame()), authData.authToken());
            sf.joinGame("WHITE", 1, authData.authToken());
            sf.logout(authData.authToken());

            // log in new user and try to join same spot
            user = new UserData("other_user", "other_password", "other_email");
            sf.register(user);
            authData = sf.login(user);
            sf.joinGame("WHITE", 1, authData.authToken());
            fail("Joined occupied spot in game");
        } catch (ResponseException e) { // should catch error when trying to join same spot
            System.out.println(e.toString());
        }
    }

    @Test
    public void testListGamesSuccess() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.createGame(new GameData(1, null,null,"game_1",new ChessGame()), authData.authToken());
            sf.createGame(new GameData(2, null,null,"game_2",new ChessGame()), authData.authToken());
            sf.createGame(new GameData(3, null,null,"game_3",new ChessGame()), authData.authToken());
            GameData[] games = sf.listGames(authData.authToken());
            assertEquals(3,games.length); // check that the list has the 3 games we created
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testListGamesFail() {
        UserData user = new UserData("my_username", "my_password", "my_email");
        try {
            sf.register(user);
            AuthData authData = sf.login(user);
            sf.createGame(new GameData(1, null,null,"game_1",new ChessGame()), authData.authToken());
            sf.createGame(new GameData(2, null,null,"game_2",new ChessGame()), authData.authToken());
            sf.createGame(new GameData(3, null,null,"game_3",new ChessGame()), authData.authToken());
            sf.logout(authData.authToken());
            sf.listGames(authData.authToken());
            fail("Listed games while logged out");
        } catch (ResponseException e) { // should catch when trying to list games while logged out
            System.out.println(e.toString());
        }
    }
}
