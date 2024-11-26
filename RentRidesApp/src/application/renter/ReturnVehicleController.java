package application.renter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.reservation.*;
import model.user.*;
import model.vehicle.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;  
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import application.menu.RenterMenuController;

public class ReturnVehicleController {

    @FXML
    private TextField registrationPlateField;  
    @FXML
    private TextField usernameField;  
    @FXML
    private DatePicker returnDatePicker; 
    @FXML
    private Button returnVehicle;
    @FXML
    private Label statusLabel;  
    @FXML
    private Button backButton;
    private Reservation reservation;
    private User user;
    private int fetchedVehicleID;
    private VehicleAdapter vehicleS=new VehicleAdapter();

	public void setReservation(Reservation reservation) {
		this.reservation=reservation;		
	}
	public void setRenter(User user) {
		this.user=user;
	}

	@FXML
	public void handleReturnVehicle() throws SQLException {
	    String registrationPlate = registrationPlateField.getText().trim();
	    String username = usernameField.getText().trim();
	    LocalDate returnDate = returnDatePicker.getValue();
	    System.out.println("Displaying rentername in return vehicle: " + user.getName()+"\n");

	    
	    if (!username.equals(user.getUsername())) {
	        showAlert("Incorrect username. Please enter the correct username.");
	        return; 
	    }

	    if (registrationPlate.isEmpty() || username.isEmpty() || returnDate == null) {
	        showAlert("Please fill in all fields!");
	        return;  
	    }

	    fetchedVehicleID = vehicleS.getVehicleIDByRegistrationPlate(registrationPlate);
	    System.out.println("Displaying fetched vehicle id inr eturn vehicle: " + fetchedVehicleID+"\n");

	    if (fetchedVehicleID == -1) {  
	        showAlert("Invalid vehicle ID. Please try again.");
	        return;  
	    }
	    String vehiclePlateFromDb = vehicleS.getVehicleNumberPlateByID(fetchedVehicleID);
	    if (!registrationPlate.equals(vehiclePlateFromDb)) {
	        showAlert("The registration plate does not match our records. Please enter the correct plate.");
	        return;
	    }
	    int renterID = reservation.getRenterIDFromReservation(reservation.getReservationID());

	    // Print information for debugging
	    System.out.println("Displaying vehicleID in return vehicle: " + fetchedVehicleID);
	    System.out.println("Displaying renterID in return vehicle: " + renterID);
	    System.out.println("Displaying vehicleID in reservation: " + reservation.getVehicleID());

	    LocalDate actualReturnDate = reservation.getEndDate();

	    if (reservation.getVehicleID() != fetchedVehicleID) {
	        showAlert("The vehicle ID does not match the reservation. Enter correct registration Plate");
	        return;  
	    }

	    if (returnDate.isAfter(actualReturnDate)) {
	        long daysLate = ChronoUnit.DAYS.between(actualReturnDate, returnDate);

	        double additionalPrice = daysLate * 10; // $10 per day late fee

	        double newPrice = reservation.getAdditionalPrice() + additionalPrice;
	        
	        reservation.updateReservationInDatabase(reservation.getReservationID(), reservation.getRenterID(), newPrice);
	        
	        showAlert("Late return charge of $" + additionalPrice + " applied.");
	    }

	    vehicleS.setVehicleAvailable(fetchedVehicleID);

	    reservation.updateMadeReservationToFalse(renterID);

	    loadFeedbackForm(reservation.getReservationID());
	}

    private void loadFeedbackForm(int reservationID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/GiveFeedback.fxml"));
            Parent root = loader.load();

            GiveFeedbackController feedbackFormController = loader.getController();
            feedbackFormController.setReservation(reservation);
            feedbackFormController.setSelectedRenter(user);
            Stage feedbackStage = new Stage();
            feedbackStage.setScene(new Scene(root));
            feedbackStage.setTitle("Provide Feedback");
            feedbackStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void showAlert(String message) {
	    Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
	    alert.setTitle("Error");
	    alert.setHeaderText("Notification");
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
