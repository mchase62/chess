package clientTests;

import client.ChessClient;
import client.websocket.NotificationHandler;
import dataAccess.*;
import exception.ResponseException;
import model.AuthData;
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


//    @Test
//    public void sampleTest() {
//        NotificationHandlerImpl handler = new NotificationHandlerImpl();
//        client = new ChessClient("http://localhost:8080",handler);
//        try {
//            String result = client.logIn("bbqyou", "bbq");
//            System.out.println("Result: " + result);
//            result = client.createGame("bbq_game1");
//            result = client.createGame("bbq_game2");
//            result = client.createGame("bbq_game3");
//            result = client.createGame("bbq_game4");
//            System.out.println("Result: " + result);
//            result = client.joinGame("1", "WHITE");
//            System.out.println("Result: " + result);
//            result = client.joinGame("3", "WHITE");
//            result = client.joinGame("4", "BLACK");
//            result = client.listGames();
//            System.out.println("Result: \n" + result);
//        } catch (ResponseException e) {
//            System.out.println(e.toString());
//        }
//        Assertions.assertTrue(true);
//    }
//    register, login, logout, createGame, joinGame, listGames,
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
            assertNotEquals(firstUser, secondUser);
        } catch (ResponseException e) {
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

}
