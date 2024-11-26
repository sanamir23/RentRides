package application.reservation;

import java.time.LocalDate;
import database.PostgreSQL;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.reservation.Reservation;

public class AddReservationController {

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
    private CheckBox driverCheckBox;

    @FXML
    private CheckBox paymentStatusCheckBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button exitButton;
    
    private int companyID;
    
    public void setCompanyID(int id) {
        this.companyID = id;
    }

    @FXML
    private void saveReservation() {
        String renterUsername = renterUsernameField.getText().trim();
        String driverUsername = driverUsernameField.getText().trim();
        String vehiclePlate = vehiclePlateField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String additionalChargesText = additionalChargesField.getText().trim();
        boolean needsDriver = driverCheckBox.isSelected();
        boolean paymentStatus = paymentStatusCheckBox.isSelected();

        if (renterUsername.isEmpty() || vehiclePlate.isEmpty() ||
                startDate == null || endDate == null || additionalChargesText.isEmpty() ||
                (needsDriver && driverUsername.isEmpty())) {
            showAlert(AlertType.ERROR, "Error", "All fields must be filled in correctly.");
            return;
        }

        double additionalCharges;
        try {
            additionalCharges = Double.parseDouble(additionalChargesText);
            if (additionalCharges < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Additional charges must be a non-negative numeric value.");
            return;
        }

        if (!endDate.isAfter(startDate)) {
            showAlert(AlertType.ERROR, "Error", "End date must be after the start date.");
            return;
        }

        int renterID = PostgreSQL.getRenterID(renterUsername);
        int vehicleID = PostgreSQL.getVehicleID(vehiclePlate);


        Reservation reservation;
        if (needsDriver) {
            int driverID = PostgreSQL.getDriverID(driverUsername);
            reservation = new Reservation(renterID, vehicleID, driverID, companyID, startDate, endDate, true, paymentStatus, additionalCharges);
            reservation.setIsDriver(needsDriver);
        } else {
            reservation = new Reservation(renterID, vehicleID, companyID, startDate, endDate, false, paymentStatus, additionalCharges);
            reservation.setIsDriver(needsDriver);
        }

        boolean isSaved = reservation.saveReservation();
        if (isSaved) {
            showAlert(AlertType.INFORMATION, "Success", "Reservation saved successfully!");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to save the reservation.");
        }
    }

    @FXML
    private void exit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        renterUsernameField.clear();
        driverUsernameField.clear();
        vehiclePlateField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        additionalChargesField.clear();
        driverCheckBox.setSelected(false);
        paymentStatusCheckBox.setSelected(false);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
