package application.driver;

import application.menu.DriverMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.user.Driver;

public class EditDriverProfileController {

	public int driverID;
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

	public void setData(int ID) {
		this.driverID = ID;
		fetchAndDisplayDriverDetails();
	}

	public void fetchAndDisplayDriverDetails() {
		Driver driver = new Driver();
		Driver fetchedDriver = driver.getDriverDetails(this.driverID);

		if (fetchedDriver != null) {
			// Populate the fields with fetched data
			nameField.setText(fetchedDriver.getName());
			emailField.setText(fetchedDriver.getEmail());
			phoneField.setText(fetchedDriver.getPhone());
			addressField.setText(fetchedDriver.getAddress());
			usernameField.setText(fetchedDriver.getUsername());
			passwordField.setText(fetchedDriver.getPassword());
			licenseField.setText(fetchedDriver.getLicenseNum());
		} else {
			showAlert(Alert.AlertType.ERROR, "Data Fetch Error", "Failed to fetch driver details.");
		}
	}

	@FXML
	private void saveChanges(ActionEvent event) {
		// Collect input values from the form fields
		String updatedName = nameField.getText().trim();
		String updatedEmail = emailField.getText().trim();
		String updatedPhone = phoneField.getText().trim();
		String updatedAddress = addressField.getText().trim();
		String updatedUsername = usernameField.getText().trim();
		String updatedPassword = passwordField.getText().trim();
		String updatedLicense = licenseField.getText().trim();

		// Validate input fields
		if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty() || updatedAddress.isEmpty()
				|| updatedUsername.isEmpty() || updatedPassword.isEmpty() || updatedLicense.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill out all the fields.");
			return;
		}

		// Check if the username contains spaces
		if (updatedUsername.contains(" ")) {
			showAlert(Alert.AlertType.ERROR, "Invalid Username", "Username cannot contain spaces.");
			return;
		}

		// Fetch current driver details
		Driver currentDriver = new Driver();
		Driver fetchedDriver = currentDriver.getDriverDetails(driverID);

		if (fetchedDriver == null) {
			showAlert(Alert.AlertType.ERROR, "Fetch Error", "Unable to fetch the driver details.");
			return;
		}

		// Track if any updates are made
		boolean isUpdated = false;
		Driver updatedDriver = new Driver(updatedName, updatedEmail, updatedPhone, updatedAddress, updatedUsername,
				updatedPassword, updatedLicense);
		updatedDriver.setUserID(driverID);

		// Update only modified attributes
		if (!updatedName.equals(fetchedDriver.getName())) {
			updatedDriver.updateDriverDetails(driverID, "driverName", updatedName);
			isUpdated = true;
		}
		if (!updatedEmail.equals(fetchedDriver.getEmail())) {
			updatedDriver.updateDriverDetails(driverID, "email", updatedEmail);
			isUpdated = true;
		}
		if (!updatedPhone.equals(fetchedDriver.getPhone())) {
			updatedDriver.updateDriverDetails(driverID, "contactNumber", updatedPhone);
			isUpdated = true;
		}
		if (!updatedAddress.equals(fetchedDriver.getAddress())) {
			updatedDriver.updateDriverDetails(driverID, "address", updatedAddress);
			isUpdated = true;
		}
		if (!updatedUsername.equals(fetchedDriver.getUsername())) {
			updatedDriver.updateDriverDetails(driverID, "username", updatedUsername);
			isUpdated = true;
		}
		if (!updatedPassword.equals(fetchedDriver.getPassword())) {
			updatedDriver.updateDriverDetails(driverID, "password", updatedPassword);
			isUpdated = true;
		}
		if (!updatedLicense.equals(fetchedDriver.getLicenseNum())) {
			updatedDriver.updateDriverDetails(driverID, "licenseNum", updatedLicense);
			isUpdated = true;
		}

		// Show success or failure alert
		if (isUpdated) {
			showAlert(Alert.AlertType.INFORMATION, "Success", "Driver details updated successfully.");
			handleExit(event);

		} else {
			showAlert(Alert.AlertType.INFORMATION, "No Changes", "No changes were made to the driver details.");
			handleExit(event);
		}
	}

	@FXML
	private void handleExit(ActionEvent event) {
		try {
			// Load the AdminMenu.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/DriverMenu.fxml"));
			Parent root = loader.load();
			DriverMenuController controller = loader.getController();
			controller.setData(driverID);

			// Open the Admin Panel in a new stage
			Stage stage = new Stage();
			stage.setTitle("Driver Panel");
			stage.setScene(new Scene(root));
			stage.show();

			// Close the current window (Manage Renter)
			((Stage) cancelButton.getScene().getWindow()).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
