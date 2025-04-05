package example.controller;

import example.demo.ConfigMain;
import example.service.ReservationService;
import example.service.TripService;
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
import example.service.EmployeeService;

import java.io.IOException;

public class LoginController {

    private static final Logger logger = LogManager.getLogger();

    private EmployeeService employeeService;

    private ReservationService reservationService;

    private TripService tripService;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public void setServices(EmployeeService employeeService, ReservationService reservationService, TripService tripService) {
        logger.trace("Setting services {} {} {}", employeeService, reservationService, tripService);
        this.employeeService = employeeService;
        this.reservationService = reservationService;
        this.tripService = tripService;
    }

    @FXML
    private void loginButtonClicked() {
        logger.traceEntry("Login button clicked");
        if (!employeeService.login(username.getText(), password.getText())) {
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
            FXMLLoader fxmlLoader = new FXMLLoader(ConfigMain.class.getResource("/views/main-view.fxml"));
            Parent root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();
            controller.setService(reservationService, tripService);
            controller.setLoggedEmployee(employeeService.getEmployee(username.getText(), password.getText()));
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

        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException("Error loading FXML", e);
        }
    }
}
