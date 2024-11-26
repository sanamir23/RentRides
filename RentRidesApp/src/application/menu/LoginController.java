package application.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import database.PostgreSQL;
import model.user.RentalCompany;
import model.user.Renter;
import model.user.Admin;
import model.user.Driver;
import model.user.User;

public class LoginController {

	protected String role;
	protected int driverID;
	protected int companyID;
	private User user;

	@FXML
	private TextField usernameTextField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private Label errorLabel;

	public void setData(String role) {
		this.role = role;
	}
	
	public void setUser(User user) {
    	System.out.print("SETTING USER Displaying userId in login controller: "+user.getUserID()+"\n");
	    this.user = user;
	}	

	@FXML
	public void handleLogin(ActionEvent event) {
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();

		if (username.isEmpty() || password.isEmpty()) {
			// Show an information alert
			showAlert(AlertType.INFORMATION, "Login Error", "Please enter both username and password.");
			return; // Stop the login process
		}

		boolean loginSuccessful = false;
		user = null;
		// Create the user object based on the role and attempt login
		if (this.role.equals("Renter")) {
			user = new Renter(username, password);
			loginSuccessful = user.login(user);
		} else if (this.role.equals("RentalCompany")) {
			user = new RentalCompany(username, password);
			loginSuccessful = user.login(user);
		} else if (this.role.equals("Driver")) {
			user = new Driver(username, password);
			loginSuccessful = user.login(user);
		} else if (this.role.equals("Admin")) {
			user = new Admin(username, password);
			loginSuccessful = user.login(user);
		}

		// Show an alert based on login success or failure
		if (loginSuccessful) {
			if (this.role.equals("Driver")) {
				this.driverID = ((Driver) user).getDriverID(((Driver) user).getUsername());
				((Driver) user).setUserID(driverID);
			}
			else if (this.role.equals("RentalCompany")) {
				this.companyID = PostgreSQL.getRentalID(((RentalCompany) user).getUsername());
				((RentalCompany) user).setUserID(companyID);
			}
			
			showAlert(AlertType.INFORMATION, "Login Successful", "Welcome to Rent Rides " + role + " !!");
			loadUserForm(this.role, event);

		} else {
			showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password.");
		}
	}

	private void loadUserForm(String role, ActionEvent event) {
		String fxmlFile = "";
		String title = "";

		switch (role) {
		case "Admin":
			fxmlFile = "/application/menu/AdminMenu.fxml";
			title = "Admin Panel";
			break;
	        case "Renter":
	            fxmlFile = "/application/menu/RenterMenu.fxml"; 
	            title = "Renter Dashboard";
	            break;
	        case "RentalCompany":
	            fxmlFile = "/application/menu/RentalCompanyMenu.fxml"; 
	            title = "Rental Company Dashboard";
	            break;
			case "Driver":
				fxmlFile = "/application/menu/DriverMenu.fxml";
				title = "Driver Dashboard";
				break;
		default:
			showAlert(Alert.AlertType.ERROR, "Error", "Unknown role: " + role);
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent root = loader.load();

			if (role == "Driver") {
				DriverMenuController driverController = loader.getController();
				driverController.setData(driverID);
			}	
			else if (role == "RentalCompany") {
				RentalCoMenuController companyController = loader.getController();
				companyController.setData(companyID);
			}
			else if(role == "Renter") {
				RenterMenuController renterMenu = loader.getController();
		        renterMenu.setRenter(user);
			}

			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(root));
			stage.show();

			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the " + title);
		}
	}

	private void showAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
