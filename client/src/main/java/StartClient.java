import gui.LoginController;
import gui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import transportAgency.objectprotocol.ServicesProxy;
import transportAgency.services.IServices;
import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {

    private static int defaultChatPort = 55555;
    private static String defaultServer = "127.0.0.1";


    public void start(Stage primaryStage) throws Exception, IOException {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/chatclient.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);
        IServices server = new ServicesProxy(serverIP, serverPort);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/login-view.fxml"));
        Parent root=loader.load();
        LoginController ctrl = loader.getController();
        ctrl.setServices(server);
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

