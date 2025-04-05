package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.services.IServices;

import java.io.IOException;

public class LoginController {

    private static final Logger logger = LogManager.getLogger();

    private MainController mainController;

    private IServices service;

    private Parent parent;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public void setServices(IServices service) {
        logger.trace("Setting service {} ",service);
        this.service = service;
    }

    @FXML
    private void loginButtonClicked() {
        logger.traceEntry("Login button clicked");
        try {
            if (!service.login(username.getText(), password.getText(), mainController)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password");
                alert.showAndWait();
                username.clear();
                password.clear();
                logger.error("Login failed");
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
            username.clear();
            password.clear();
            logger.error("Login failed");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
            Parent root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();
            controller.setServices(service);
            controller.setLoggedEmployee(service.getEmployee(username.getText(), password.getText()));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Main");
            stage.sizeToScene();
            stage.show();
            username.clear();
            password.clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Login successful!");
            alert.showAndWait();
            logger.trace("Login successful");

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
