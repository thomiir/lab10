package example.controller;

import example.domain.Employee;
import example.domain.ReservationDTO;
import example.domain.Trip;
import example.service.ReservationService;
import example.service.TripService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private TripService tripService;

    private ReservationService reservationService;

    private Employee loggedEmployee;

    @FXML
    private TextField destinationField, timeField, clientField, noSeatsField;

    @FXML
    private Label employeeLabel;

    @FXML
    private DatePicker dateField;

    @FXML
    private TableColumn<ReservationDTO, Integer> seatNo;

    @FXML
    private TableColumn<ReservationDTO, String> clientName;

    @FXML
    private TableView<ReservationDTO> reservationTable;

    @FXML
    private TableView<Trip> tripTable;

    @FXML
    private TableColumn<Trip, String> destination, date, time;

    @FXML
    private TableColumn<Trip, Integer> noSeats;

    public void setService(ReservationService reservationService, TripService tripService) {
        this.reservationService = reservationService;
        this.tripService = tripService;
        reloadTripTable();
    }

    @FXML
    private void findTripButtonClicked() {
        logger.traceEntry("Trip button clicked");
        String destination = destinationField.getText();
        String date = dateField.getValue().toString();
        String time = timeField.getText();

        System.out.println(tripService.tripExists(destination, date, time));
        if (!tripService.tripExists(destination, date, time)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Nu exista aceasta calatorie.");
            alert.showAndWait();
            logger.error("No trip was found");
            destinationField.clear();
            clientField.clear();
            noSeatsField.clear();
        }
        else {
            reservationTable.getItems().clear();
            reservationTable.getItems().addAll(reservationService.findAllReservedSeats(destination, date, time));
            destinationField.clear();
            dateField.setValue(null);
            timeField.clear();
            logger.traceExit();
        }
    }

    @FXML
    private void makeReservationButtonClicked() {
        logger.traceEntry("Make a reservation button clicked");
        if (tripTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Selectati o calatorie");
            alert.showAndWait();
            logger.error("No trip was selected");
            clientField.clear();
            noSeatsField.clear();
            return;
        }

        if (noSeats.getText().isEmpty() || clientField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Completati numarul de locuri!\nCompletati numele clientului!");
            alert.showAndWait();
            logger.error("ClientName or noSeats was null");
            clientField.clear();
            noSeatsField.clear();
            return;
        }

        String clientName = clientField.getText();
        Integer noSeats = Integer.valueOf(noSeatsField.getText());
        Long tripId = tripTable.getSelectionModel().getSelectedItem().getId();
        try
        {
            reservationService.makeReservation(clientName, noSeats, tripId);
            reloadReservationTable(tripTable.getSelectionModel().getSelectedItem());
            reloadTripTable();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Rezervare facuta cu succes!");
            alert.showAndWait();
            logger.info("Make a reservation success");
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            logger.error(e);
        }
        finally {
            clientField.clear();
            noSeatsField.clear();
        }
        logger.traceExit("Reservation made!");
    }

    private void reloadTripTable() {
        tripTable.getItems().clear();
        tripTable.getItems().addAll(tripService.getAllTrips());
    }

    private void reloadReservationTable(Trip trip) {
        reservationTable.getItems().clear();
        reservationTable.getItems().addAll(reservationService.findAllReservedSeats(trip.getDestination(),trip.getDepartureDate().toString(), trip.getDepartureTime().toString()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.traceEntry("Initialize main example.controller with {} {}", reservationService, tripService);
        seatNo.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().seatNo()));
        clientName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().clientName()));
        destination.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDestination()));
        date.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDepartureDate().toString()));
        time.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDepartureTime().toString()));
        noSeats.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNoSeatsAvailable()));
        logger.traceExit();
    }

    public void setLoggedEmployee(Employee loggedEmployee) {
        logger.trace("employee logged {}", loggedEmployee);
        this.loggedEmployee = loggedEmployee;
        employeeLabel.setText("Angajat: " + loggedEmployee.getUsername());
    }

    @FXML
    private void logoutButtonClicked() {
        logger.traceEntry("Logout button clicked");
        this.loggedEmployee = null;
        Stage stage = (Stage) dateField.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setContentText("Logout successful!");
        alert.showAndWait();
        stage.close();
        logger.traceExit("Logout successful");
    }
}

