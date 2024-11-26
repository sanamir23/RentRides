package application.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class ChooseRoleController {

	public String role;
	public String option;

	@FXML
	private CheckBox renterCheckBox;

	@FXML
	private CheckBox rentalCompanyCheckBox;

	@FXML
	private CheckBox driverCheckBox;

	@FXML
	private CheckBox adminCheckBox;

	@FXML
	private Button btn1;

	@FXML
	private Label errorLabel;

	@FXML
	public void initialize() {
		renterCheckBox.setOnAction(event -> {
			if (renterCheckBox.isSelected()) {
				rentalCompanyCheckBox.setSelected(false);
				driverCheckBox.setSelected(false);
				adminCheckBox.setSelected(false);
				role = "Renter";
			}
		});

		rentalCompanyCheckBox.setOnAction(event -> {
			if (rentalCompanyCheckBox.isSelected()) {
				renterCheckBox.setSelected(false);
				driverCheckBox.setSelected(false);
				adminCheckBox.setSelected(false);
				role = "RentalCompany";
			}
		});

		driverCheckBox.setOnAction(event -> {
			if (driverCheckBox.isSelected()) {
				renterCheckBox.setSelected(false);
				rentalCompanyCheckBox.setSelected(false);
				adminCheckBox.setSelected(false);
				role = "Driver";
			}
		});

		adminCheckBox.setOnAction(event -> {
			if (adminCheckBox.isSelected()) {
				renterCheckBox.setSelected(false);
				rentalCompanyCheckBox.setSelected(false);
				driverCheckBox.setSelected(false);
				role = "Admin";
			}
		});
	}

	@FXML
	public void handleContinue(ActionEvent event) {
		if (role == null || role.isEmpty()) {
			// Show an alert if no role is selected
			showAlert(AlertType.WARNING, "Error", "Please select a role before continuing.");
		} else if ("Register".equals(this.option)) {
			loadScene("/application/menu/Register.fxml", "Register", event);
		} else if ("Login".equals(this.option)) {
			loadScene("/application/menu/Login.fxml", "Login", event);
		}
	}

	// Helper method to display alerts
	private void showAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void loadScene(String fxmlFile, String title, ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent root = loader.load();

			// Set role in the target controller
			if ("/application/menu/Register.fxml".equals(fxmlFile)) {
				RegisterController registerController = loader.getController();
				registerController.setData(role);
			} else if ("/application/menu/Login.fxml".equals(fxmlFile)) {
				LoginController loginController = loader.getController();
				loginController.setData(role);
			}

			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(root));
			stage.show();

			((Stage) btn1.getScene().getWindow()).close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOption(String option) {
		this.option = option;
	}
}
