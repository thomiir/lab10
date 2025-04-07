package transportAgency.gui;

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

public class LoginController {

    private static final Logger logger = LogManager.getLogger();

    private IServices service;

    private Parent parent;

    private MainController mainController;

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
            mainController.setServices(service);
            mainController.setLoggedEmployee(service.getEmployee(username.getText(), password.getText()));
            service.login(username.getText(), password.getText(), mainController);
            mainController.reloadTripTable();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            logger.error("Login failed: {}", e.getMessage());
            logger.error(e);
        }
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
