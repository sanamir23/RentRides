package application.rentalco;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.user.Driver;
import database.PostgreSQL;

public class EditDriverController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField licenseField;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Driver driver;
    private CompanyDriversController parentController;

    public void setDriver(Driver driver, CompanyDriversController parentController) {
        this.driver = driver;
        this.parentController = parentController;

        // Populate form fields with driver details
        nameField.setText(driver.getName());
        emailField.setText(driver.getEmail());
        phoneField.setText(driver.getPhone());
        addressField.setText(driver.getAddress());
        usernameField.setText(driver.getUsername());
        passwordField.setText(driver.getPassword());
        licenseField.setText(driver.getLicenseNum());
    }

    @FXML
    private void saveChanges() {
        // Update driver details
        driver.setName(nameField.getText());
        driver.setEmail(emailField.getText());
        driver.setPhone(phoneField.getText());
        driver.setAddress(addressField.getText());
        driver.setUsername(usernameField.getText());
        driver.setPassword(passwordField.getText());
        driver.setLicenseNum(licenseField.getText());

        // Update the driver in the database
        boolean success = PostgreSQL.updateDriver(driver);

        // Show success or failure message
        if (success) {
            showAlert("Success", "Driver updated successfully!", "The driver's details have been saved.", Alert.AlertType.INFORMATION);
            parentController.loadDrivers(); // Refresh the list in the parent controller
        } else {
            showAlert("Error", "Failed to update driver!", "An error occurred while saving the driver's details.", Alert.AlertType.ERROR);
        }

        closeWindow();
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
