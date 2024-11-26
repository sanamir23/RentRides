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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.reservation.*;

public class ManageCustomerSupportController {
	@FXML
    private TableView<CustomerSupport> supportTable;

    @FXML
    private TableColumn<CustomerSupport, Integer> supportID;

    @FXML
    private TableColumn<CustomerSupport, Integer> renterID;

    @FXML
    private TableColumn<CustomerSupport, String> issueType;

    @FXML
    private TableColumn<CustomerSupport, String> issueDescription;

    @FXML
    private TableColumn<CustomerSupport, String> status;

    @FXML
    private TableColumn<CustomerSupport, String> response;

    @FXML
    private TableColumn<CustomerSupport, String> createdAt;
    
    
    @FXML
    private TextArea responseField;
    
    @FXML
    private ComboBox<String> statusOfSupport;
    
    
    @FXML
    private Button exitButton;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private TextField deleteID;
    
    
    public void initialize() {
        // Initialize the table columns to match the properties of CustomerSupport
        supportID.setCellValueFactory(new PropertyValueFactory<>("supportID"));
        renterID.setCellValueFactory(new PropertyValueFactory<>("renterID"));
        issueType.setCellValueFactory(new PropertyValueFactory<>("issueType"));
        issueDescription.setCellValueFactory(new PropertyValueFactory<>("issueDescription"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        response.setCellValueFactory(new PropertyValueFactory<>("response"));
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
       
        issueDescription.setCellFactory(tc -> {
            return new TableCell<CustomerSupport, String>() {
                private Tooltip tooltip = new Tooltip();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        tooltip.setText(item); // Set the tooltip text
                        setTooltip(tooltip);  // Set the tooltip to the cell
                    }
                }
            };
        });
        
        CustomerSupport cs = new CustomerSupport();
        ObservableList<CustomerSupport> listOfCS = cs.fetchCustomerSupportData();
        supportTable.setItems(listOfCS);
        statusOfSupport.getItems().addAll("Resolved");
    }
    
    @FXML
    private void handleUpdate(ActionEvent event) {
        // Get the selected CustomerSupport object
        CustomerSupport selectedSupport = supportTable.getSelectionModel().getSelectedItem();
        if (selectedSupport == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a support ticket to update.");
            return;
        }
        
        String updatedResponse = responseField.getText();
        String updatedStatus = statusOfSupport.getValue();
        
        if (updatedResponse == null || updatedResponse.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Response field cannot be empty.");
            return;
        }

        if (updatedStatus == null || updatedStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Status field cannot be empty.");
            return;
        }
        
        selectedSupport.setResponse(updatedResponse);
        selectedSupport.setStatus(updatedStatus);
        
        selectedSupport.updateSupport(selectedSupport.getSupportID(),updatedResponse,updatedStatus);
        
        refreshTable();
    }

    @FXML 
    private void handleDelete(ActionEvent event)
    {
    	String supportIDInput = deleteID.getText();

        // Check if the input is empty
        if (supportIDInput.isEmpty() || !isInteger(supportIDInput)) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric Support ID.");
            return;
        }

        // Check if the input is a valid integer
        try {
            int ID = Integer.parseInt(supportIDInput);
            CustomerSupport cs = new CustomerSupport();
            cs.deleteCustomerSupport(ID);

            refreshTable();
        } catch (NumberFormatException e) {
            // Show an error if the input is not a valid integer
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Renter ID must be a numeric value.");
        }
    }
    
    private void refreshTable() {
    	supportTable.getItems().clear();
    	
        CustomerSupport cs = new CustomerSupport();
        
        ObservableList<CustomerSupport> listOfCS = cs.fetchCustomerSupportData(); 
        supportTable.getItems().addAll(listOfCS);
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
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
