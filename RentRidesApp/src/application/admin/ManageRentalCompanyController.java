package application.admin;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.user.*;

public class ManageRentalCompanyController {
	@FXML
    private TableView<User> companyTable;

    @FXML
    private TableColumn<User, Integer> companyID;

    @FXML
    private TableColumn<User, String> name;

    @FXML
    private TableColumn<User, String> email;
    
    @FXML
    private TableColumn<User, String> contactNumber;
    
    @FXML
    private TableColumn<User, String> address;
    
    @FXML
    private TableColumn<User, String> username;
    
    @FXML
    private TableColumn<User, String> password;
    
    @FXML
    private TableColumn<User, String> status;
    
    @FXML 
    private TextField deleteID;
    
    @FXML
    private Button deleteButton;
    
    @FXML 
    private ComboBox<String> attributeComboBox;
    
    @FXML 
    private TextField editID;
    
    @FXML 
    private TextField newValue;
    
    @FXML
    private Button editButton;
    
    
    @FXML
    private Button exitButton;
    
    @FXML
    private void initialize()
    {
    	companyID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        status.setCellValueFactory(new PropertyValueFactory<>("CompanyStatus"));
        
        User user = new RentalCompany();
        ObservableList<User> companies = ((RentalCompany) user).displayAllCompanies();
        companyTable.setItems(companies);
        
        attributeComboBox.getItems().addAll("companyName", "email", "contactNumber", "address", "password");
    }
    
    @FXML
    private void handleDelete(ActionEvent event) {
        String companyIDInput = deleteID.getText();

        // Check if the input is empty
        if (companyIDInput.isEmpty() || !isInteger(companyIDInput)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric Renter ID.");
            return;
        }

        // Check if the input is a valid integer
        try {
            int ID = Integer.parseInt(companyIDInput);
            User user = new RentalCompany();
            ((RentalCompany) user).deleteRentalCompany(ID);

            refreshTable();
        } catch (NumberFormatException e) {
            // Show an error if the input is not a valid integer
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rental ID must be a numeric value.");
        }
    }
    
    private void refreshTable() {
    	companyTable.getItems().clear();

        // Create a Renter object and fetch data from the database
        User user = new RentalCompany();
        ObservableList<User> companies = ((RentalCompany) user).displayAllCompanies(); // Ensure displayAllRenters() returns ObservableList<User>

        // Add the fetched data to the TableView
        companyTable.getItems().addAll(companies);
    }
    
    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @FXML
    private void handleEdit(ActionEvent event)
    {
    	String selectedAttribute = attributeComboBox.getValue();
        String updatedValue = newValue.getText();
        String companyIDInput = editID.getText();
        
        if(companyIDInput.isEmpty() || !isInteger(companyIDInput))
        {
        	 showAlert(Alert.AlertType.ERROR, "Input Error", "Please provide a valid numeric ID");
             return;
        }

        if (selectedAttribute == null || updatedValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please select an attribute and enter a new value.");
            return;
        }
        
        
        try {
            int ID = Integer.parseInt(companyIDInput);
            User user = new RentalCompany();
            ((RentalCompany) user).updateRentalCompany(ID, selectedAttribute, updatedValue);

            refreshTable();
        } catch (NumberFormatException e) {
            // Show an error if the input is not a valid integer
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Company ID must be a numeric value.");
        }
        
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        try {
            // Load the AdminMenu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/AdminMenu.fxml"));
            Parent root = loader.load();

            // Open the Admin Panel in a new stage
            Stage stage = new Stage();
            stage.setTitle("Admin Panel");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (Manage Renter)
            ((Stage) exitButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper Method to Show Alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    


}
