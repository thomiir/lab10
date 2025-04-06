package gui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transportAgency.model.Employee;
import transportAgency.model.Reservation;
import transportAgency.model.Seat;
import transportAgency.model.Trip;
import transportAgency.services.IObserver;
import transportAgency.services.IServices;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable, IObserver {
    private static final Logger logger = LogManager.getLogger();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private IServices service;
    private Employee loggedEmployee;

    @FXML private TextField destinationField, timeField, clientField, noSeatsField;
    @FXML public Label employeeLabel;
    @FXML private DatePicker dateField;
    @FXML private TableView<Seat> reservationTable;
    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Seat, Integer> seatNo;
    @FXML private TableColumn<Seat, String> clientName;
    @FXML private TableColumn<Trip, String> destination, date, time;
    @FXML private TableColumn<Trip, Integer> noSeats;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.trace("Initializing main controller");
        try {
            seatNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().seatNo()));
            clientName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().clientName()));

            destination.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDestination()));
            date.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDepartureDate().toString()));
            time.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDepartureTime().toString()));
            noSeats.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNoSeatsAvailable()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServices(IServices service) {
        logger.trace("Setting service for controller");
        this.service = service;
    }

    public void setLoggedEmployee(Employee loggedEmployee) {
        logger.info("Setting logged employee: {}", loggedEmployee.getUsername());
        this.loggedEmployee = loggedEmployee;
        employeeLabel.setText("Employee: " + loggedEmployee.getUsername());
    }

    @FXML
    private void findTripButtonClicked() {
        logger.trace("Find trip button clicked");

        String destination = destinationField.getText();
        String date = dateField.getValue() != null ? dateField.getValue().toString() : "";
        String time = timeField.getText();

        if (destination.isEmpty() || date.isEmpty() || time.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields");
            return;
        }

        try {
            Seat[] seats = service.findAllReservedSeats(destination, date, time);
            Platform.runLater(() -> {
                reservationTable.getItems().clear();
                reservationTable.getItems().addAll(seats);
            });
        } catch (Exception e) {
            logger.error("Error finding trip", e);
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        } finally {
            destinationField.clear();
            dateField.setValue(null);
            timeField.clear();
        }
    }

    @FXML
    private void makeReservationButtonClicked() {
        logger.trace("Make reservation button clicked");

        Trip selectedTrip = tripTable.getSelectionModel().getSelectedItem();
        if (selectedTrip == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a trip");
            return;
        }

        if (clientField.getText().isEmpty() || noSeatsField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please enter client name and number of seats");
            return;
        }

        try {
            String clientName = clientField.getText();
            int seats = Integer.parseInt(noSeatsField.getText());
            System.out.println("before service make reserv");
            service.makeReservation(clientName, seats, selectedTrip);
            System.out.println("after service make reserv");
//            Reservation reservation = new Reservation(-1L, clientName, seats, selectedTrip);
//            System.out.println("before reload made");
//            reservationMade(reservation);
//            System.out.println("after reload made");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation made successfully");
            clientField.clear();
            noSeatsField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid number of seats");
        } catch (Exception e) {
            logger.error("Error making reservation", e);
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void logoutButtonClicked() {
        logger.trace("Logout button clicked");
        try {
            service.logout(loggedEmployee, this);
            Stage stage = (Stage) dateField.getScene().getWindow();
            stage.close();
            logger.info("Logout successful for user: {}", loggedEmployee.getUsername());
        } catch (Exception e) {
            logger.error("Error during logout", e);
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Failed to logout");
        }
    }

    @Override
    public void reservationMade(Reservation reservation) {
        System.out.println(this);
        Platform.runLater(() -> {
            try {
                System.out.println("before trip");
                Trip selected = tripTable.getSelectionModel().getSelectedItem();
                System.out.println("before trip reload");
                reloadTripTable();
                System.out.println("after trip reload");
                if (selected != null) {
                    reloadReservationTable(selected);
                }
                System.out.println("after reservation reload");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to refresh data");
            }
        });
    }

    public void reloadTripTable() {
        executor.execute(() -> {
            try {
                Trip[] trips = service.getAllTrips();
                Platform.runLater(() -> {
                    try {
                        tripTable.getItems().setAll(Arrays.asList(trips));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR,"Error", e.getMessage()));
            }
        });
    }

    private void reloadReservationTable(Trip trip) {
        executor.execute(() -> {
            try {
                Seat[] seats = service.findAllReservedSeats(
                        trip.getDestination(),
                        trip.getDepartureDate().toString(),
                        trip.getDepartureTime().toString()
                );
                Platform.runLater(() -> {
                    reservationTable.getItems().setAll(seats);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR,"Error", "Failed to load seats"));
            }
        });
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}