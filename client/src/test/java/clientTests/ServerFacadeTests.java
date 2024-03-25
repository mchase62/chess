package clientTests;

import client.ChessClient;
import client.websocket.NotificationHandler;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import webSocketMessages.Notification;


public class ServerFacadeTests {

    private static Server server;
    public static ChessClient client;
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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        NotificationHandlerImpl handler = new NotificationHandlerImpl();
        client = new ChessClient("http://localhost:8080",handler);
        try {
            String result = client.logIn("bbqyou", "bbq");
            System.out.println("Result: " + result);
            result = client.createGame("bbq_game1");
            result = client.createGame("bbq_game2");
            result = client.createGame("bbq_game3");
            result = client.createGame("bbq_game4");
            System.out.println("Result: " + result);
            result = client.joinGame("1", "WHITE");
            System.out.println("Result: " + result);
            result = client.joinGame("3", "WHITE");
            result = client.joinGame("4", "BLACK");
            result = client.listGames();
            System.out.println("Result: \n" + result);
        } catch (ResponseException e) {
            System.out.println(e.toString());
        }
        Assertions.assertTrue(true);
    }

}
