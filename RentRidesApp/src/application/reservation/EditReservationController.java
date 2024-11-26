package application.reservation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.reservation.Reservation;

import database.PostgreSQL;

public class EditReservationController {

    @FXML
    private TextField renterUsernameField;

    @FXML
    private TextField driverUsernameField;

    @FXML
    private TextField vehiclePlateField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField additionalChargesField;

    @FXML
    private CheckBox paymentStatusCheckBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Reservation reservationToEdit;

    @FXML
    private void initialize() {
        // Handle save button
        saveButton.setOnAction(event -> saveChanges());

        // Handle cancel button
        cancelButton.setOnAction(event -> cancelEdit());
    }

    public void setReservationToEdit(Reservation reservation) {
        this.reservationToEdit = reservation;
        renterUsernameField.setText(PostgreSQL.getRenterUsername(reservation.getRenterID()));
        driverUsernameField.setText(PostgreSQL.getDriverUsername(reservation.getDriverID()));
        vehiclePlateField.setText(PostgreSQL.getVehicleNum(reservation.getVehicleID()));
        startDatePicker.setValue(reservation.getStartDate());
        endDatePicker.setValue(reservation.getEndDate());
        additionalChargesField.setText(String.valueOf(reservation.getAdditionalCharges()));
        paymentStatusCheckBox.setSelected(reservation.isPaymentStatus());
    }

    private void saveChanges() {
        try {
            // Fetch IDs using PostgreSQL methods
            int renterID = PostgreSQL.getRenterID(renterUsernameField.getText());
            int vehicleID = PostgreSQL.getVehicleID(vehiclePlateField.getText());
            int driverID = PostgreSQL.getDriverID(driverUsernameField.getText());

            // Check if renterID or vehicleID is -1 and show an alert
            if (renterID == -1) {
                showAlert(Alert.AlertType.ERROR, "Invalid Renter", "The renter username does not exist. Please enter a valid username.");
                return;
            }

            if (vehicleID == -1) {
                showAlert(Alert.AlertType.ERROR, "Invalid Vehicle", "The vehicle plate number does not exist. Please enter a valid plate number.");
                return;
            }

            // Set the updated fields for the reservation
            reservationToEdit.setRenterID(renterID);
            reservationToEdit.setVehicleID(vehicleID);
            reservationToEdit.setDriverID(driverID);
            reservationToEdit.setStartDate(startDatePicker.getValue());
            reservationToEdit.setEndDate(endDatePicker.getValue());
            reservationToEdit.setAdditionalCharges(Double.parseDouble(additionalChargesField.getText()));
            reservationToEdit.setPaymentStatus(paymentStatusCheckBox.isSelected());

            // Save the updated reservation to the database
            if (reservationToEdit.updateReservation()) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update the reservation.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please ensure all fields are filled correctly, especially numerical values.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void cancelEdit() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        System.out.println("Edit canceled.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}