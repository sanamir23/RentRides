package application.renter;

import model.reservation.CustomerSupport;
import application.menu.RenterMenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.user.User;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
public class ContactSupportController {

    @FXML
    private TextField renterIDField;

    @FXML
    private ComboBox<String> issueTypeComboBox;
    @FXML
    private Button backButton;
    @FXML
    private TextArea issueDescriptionArea;
    private User user;
    
    @FXML
    public void initialize() {
        issueTypeComboBox.getItems().addAll("Payment", "Reservation", "Technical Issue", "Other");

        issueTypeComboBox.getSelectionModel().select(0); // select the first item by default
    }
    public void setRenter(User user) {
		this.user=user;
	}
    @FXML
    private void handleSubmit() {
        String username = renterIDField.getText().trim();
        String issueType = issueTypeComboBox.getValue();
        String issueDescription = issueDescriptionArea.getText().trim();
        System.out.print("Printing renter id in customer support: "+user.getUsername()+"\n");
        CustomerSupport support = new CustomerSupport(username, issueType, issueDescription);

        if (username.isEmpty() || issueType == null || issueDescription.isEmpty()) {
            showAlert(AlertType.WARNING, "Form Incomplete", "Please fill all the fields.");
            return;
        }
        if (!username.equals(user.getUsername())) {
            showAlert(AlertType.WARNING, "Incorrect Username", "Please enter the correct username.");
            return;
        }

        showAlert(AlertType.INFORMATION, "Submission Successful", "Your issue has been submitted successfully!");

        renterIDField.clear();
        issueTypeComboBox.getSelectionModel().select(0);
        issueDescriptionArea.clear();
        boolean success = support.saveToDatabase(user.getUserID(),username, issueType, issueDescription);
        if(success) {
        	showAlert(AlertType.CONFIRMATION,"Issue Considered!"," Get Back Later");
            closeScreen();
        }
        else {
        	showAlert(AlertType.ERROR,"Form Submission Failed!"," Try Again");
        }
        
    }
    private void closeScreen() {
        Stage currentStage = (Stage) renterIDField.getScene().getWindow();
        currentStage.close();
    }
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 @FXML
	
    public void handleBackAction() {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/RenterMenu.fxml"));
			Parent root = loader.load();
			RenterMenuController controller = loader.getController();
			controller.setRenter(user);
			
			// Open the Admin Panel in a new stage
			Stage stage = new Stage();
			stage.setTitle("Renter Panel");
			stage.setScene(new Scene(root));
			stage.show();

			// Close the current window (Manage Renter)
			((Stage) backButton.getScene().getWindow()).close();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
