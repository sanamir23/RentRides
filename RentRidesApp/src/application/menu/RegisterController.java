package application.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import model.user.RentalCompany;
import model.user.Renter;
import model.user.Admin;
import model.user.Driver;
import model.user.User;

public class RegisterController {

	protected String role;

	@FXML
	private TextField nameTextField;

	@FXML
	private TextField emailTextField;

	@FXML
	private TextField phoneTextField;

	@FXML
	private TextField addressTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField passwordTextField;

	@FXML
	private TextField licenseTextField;

	@FXML
	private TextField companyUsernameTextField;

	@FXML
	private ImageView eyeIcon;

	private boolean isPasswordVisible = false;

	public void initialize() {
		//this.licenseLabel.setVisible(false);
		this.licenseTextField.setVisible(false);
		this.passwordTextField.setVisible(false);
		//this.companyUsernameLabel.setVisible(false);
		this.companyUsernameTextField.setVisible(false);

		this.nameTextField.setTooltip(new Tooltip("Enter your full name. Avoid special characters or numbers."));
		this.emailTextField.setTooltip(new Tooltip("Enter your email in the format: user@example.com"));
		this.phoneTextField.setTooltip(new Tooltip("Enter your phone number with country code (e.g., +1234567890)"));
		this.addressTextField.setTooltip(new Tooltip("Enter your residential address including street and city."));
		this.usernameTextField
				.setTooltip(new Tooltip("Choose a unique username without spaces or special characters."));
		this.passwordField.setTooltip(
				new Tooltip("Enter a strong password (at least 8 characters, including letters and numbers)."));
		this.licenseTextField.setTooltip(new Tooltip("Enter your driver's license number (if applicable)."));

		this.passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
		this.eyeIcon.setOnMouseClicked(event -> togglePasswordVisibility());
	}

	private void togglePasswordVisibility() {
		if (isPasswordVisible) {
			// Hide password (show PasswordField, hide TextField)
			passwordTextField.setVisible(false);
			passwordField.setVisible(true);
			isPasswordVisible = false;
		} else {
			// Show password (show TextField, hide PasswordField)
			passwordTextField.setVisible(true);
			passwordField.setVisible(false);
			isPasswordVisible = true;
		}
	}

	@FXML
	public void handleRegisteration(ActionEvent event) {
		String name = nameTextField.getText();
		String email = emailTextField.getText();
		String phone = phoneTextField.getText();
		String address = addressTextField.getText();
		String username = usernameTextField.getText();
		String password = passwordField.getText();

		// Check if any required field is empty
		if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || username.isEmpty()
				|| password.isEmpty()) {
			showAlert(AlertType.WARNING, "Incomplete Information", "Please fill out all the required fields.");
			return;
		}
		if(email.contains(" "))
		{
			showAlert(AlertType.ERROR, "Invalid Email", "Email cannot contain spaces.");
			return;
		}
		if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
	        showAlert(Alert.AlertType.ERROR, "Input Error", "Please provide a valid email address.");
	        return;
	    }
		 if (!phone.matches("\\d{11}")) {
		        showAlert(AlertType.ERROR, "Invalid Phone Number", "Phone number must be exactly 11 digits.");
		        return; 
		    }
		if (username.contains(" ")) {
			showAlert(AlertType.ERROR, "Invalid Username", "Username cannot contain spaces.");
			return;
		}
		
		// Create and register the user based on the selected role
		User user = null;
		boolean registrationSuccessful = false;

		if (this.role.equals("Renter")) {
			user = new Renter(name, email, phone, address, username, password, false);
			registrationSuccessful = user.register(user);
		} 
		
		else if (this.role.equals("RentalCompany")) {
			user = new RentalCompany(name, email, phone, address, username, password);
			registrationSuccessful = user.register(user);
		} 
		
		else if (this.role.equals("Driver")) {

			String license = licenseTextField.getText();
			String companyUsername = companyUsernameTextField.getText();

			if (license.isEmpty() || companyUsername.isEmpty()) {
				showAlert(AlertType.WARNING, "Incomplete Information", "Please fill out all the required fields.");
				return;
			}

			user = new Driver(name, email, phone, address, username, password, license, companyUsername, "Pending");
			registrationSuccessful = user.register(user);

		} 
		
		else if (this.role.equals("Admin")) {
			user = new Admin(name, email, phone, address, username, password);
			registrationSuccessful = user.register(user);
		}

		if (registrationSuccessful) {
			showAlert(AlertType.INFORMATION, "Registration Successful",
					"Your account has been created. Kindly log in.");

			// Load and show the login form
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/Login.fxml"));
				Parent root = loader.load();

				LoginController loginController = loader.getController();
				loginController.setData(role);

				Stage stage = new Stage();
				stage.setTitle("Login");
				stage.setScene(new Scene(root));
				stage.show();

				// Close the registration form
				((Stage) nameTextField.getScene().getWindow()).close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void setData(String role) {
		this.role = role;
		if (role.equals("Driver")) {
			//this.licenseLabel.setVisible(true);
			this.licenseTextField.setVisible(true);
			//this.companyUsernameLabel.setVisible(true);
			this.companyUsernameTextField.setVisible(true);
		}
	}
}
