package application.menu;

import java.io.IOException;
import java.sql.SQLException;

import application.renter.BrowseVehiclesController;
import application.renter.ContactSupportController;
import application.renter.ReturnVehicleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.reservation.Reservation;
import model.user.Renter;
import model.user.User;

//Add this:
import javafx.scene.control.Button;

public class RenterMenuController {
	
    private User user;
    private Reservation reservation = new Reservation();
    @FXML
    public Button browseVehicleBtn;
    @FXML
    public Button contactSupportButton;
    @FXML
    public Button returnVehicleButton;
	public void setRenter(User renter) {
		if (renter != null) {
	        this.user = renter;
	        System.out.println("User set in RenterMenuController: " + user.getUserID());
	    } else {
	        System.out.println("User is null when setting in RenterMenuController.");
	    }
	}
    public void initialize() {
    }
    @FXML
    private void browseVehicle() throws IOException {
    	if (user == null) {
            showAlert("Error", "User is not logged in.");
            return;
        }
    	System.out.print("Displaying userId in renter menu before controller: " + user.getUserID()+"\n");
    	 // Close the current stage (window)
        Stage currentStage = (Stage) browseVehicleBtn.getScene().getWindow(); // get the current stage from the button
        currentStage.close();  // close the current window
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/BrowseVehicles.fxml"));
        Parent root = loader.load();
        BrowseVehiclesController mc = loader.getController();
    	System.out.print("Displaying userId in login controller: " + user.getUserID()+"\n");
    	mc.setRenter(user);
    	
    	
        Stage browseStage = new Stage();
        Scene scene = new Scene(root);
        browseStage.setTitle("Rent Rides Application");
        browseStage.setScene(scene);
        browseStage.show();
    }
    
    @FXML
    private void contactSupport() throws IOException {
        showAlert("Contact Customer Support", "Customer support is available to assist you.");
        if (user == null) {
            System.err.println("User is not initialized!");
            return;
        }
        int userID = user.getUserID(); 
        System.out.println("User ID: " + userID);
        
        // Close the current stage
        Stage currentStage = (Stage) contactSupportButton.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/ContactCustomerSupport.fxml"));
        Parent root = loader.load();
        ContactSupportController supportController = loader.getController();

        System.out.print("Printing renter id in customer support: "+user.getUserID()+"\n");

        supportController.setRenter(user);

        Stage returnStage = new Stage();
        Scene scene = new Scene(root);
        returnStage.setTitle("Return Vehicle");
        returnStage.setScene(scene);
        returnStage.show();
    }

    @FXML
    private void returnVehicle() throws SQLException {
    	 if (user == null) {
             showAlert("Error", "User is not logged in.");
             return;
         }
    	try {
    		
            int renterID = user.getUserID();
            boolean hasActiveReservation = reservation.checkMadeReservation(renterID);

            if (!hasActiveReservation) {
                showAlert("No Active Reservation", "You do not have any active reservations.");
                return;
            }
            Reservation reserved=reservation.getActiveReservationByRenterID(renterID);
            if (reserved == null) {
                showAlert("No Active Reservation", "You do not have any active reservations to return.");
                return;
            }
            // Close the current stage
            Stage currentStage = (Stage) returnVehicleButton.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/ReturnVehicle.fxml"));
            Parent root = loader.load();
            ReturnVehicleController returnController = loader.getController();
    	    System.out.println("Displaying rentername in rentermenu: " + user.getUsername()+"\n");

            returnController.setRenter(user);
            returnController.setReservation(reserved);

            Stage returnStage = new Stage();
            Scene scene = new Scene(root);
            returnStage.setTitle("Return Vehicle");
            returnStage.setScene(scene);
            returnStage.show();

            showAlert("Return Vehicle", "Please fill in the details to return your vehicle.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while opening the return vehicle form.");
        }
        
    }

    @FXML
    private void showCurrentReservations() {
    	 if (user == null) {
             showAlert("Error", "User is not logged in.");
             return;
         }
    	 try {
             int renterID = user.getUserID(); 

             boolean hasActiveReservation = reservation.checkMadeReservation(renterID);

             if (!hasActiveReservation) {
                 showAlert("No Active Reservation", "You do not have any active reservations.");
                 return;
             }
             System.out.print("Displaying renter ID in rentermennu: "+ renterID+"\n");
             Reservation reserved = reservation.getActiveReservationByRenterID(renterID);

             if (reserved == null) {
                 showAlert("No Reservations", "You do not have any active reservations.");
                 return;
             }
             System.out.print("Displaying reservation ID in rentermennu: "+ reserved.getReservationID()+"\n");
             Renter renter = (Renter) user;  // Casting the User object to Renter

             String renterName = renter.getRenterNameByID(renterID);
             String vehicleName = renter.getVehicleNameByID(reserved.getVehicleID());
             double paymentAmount = reservation.getPaymentAmountByReservationID(reserved.getReservationID());

             StringBuilder reservationDetails = new StringBuilder("Your Current Reservation:\n\n");
             reservationDetails.append("Reservation ID: ").append(reserved.getReservationID()).append("\n")
             				   .append("Name: ").append(renterName).append("\n")
                               .append("Vehicle Name: ").append(vehicleName).append("\n")
                               .append("Start Date: ").append(reserved.getStartDate()).append("\n")
                               .append("End Date: ").append(reserved.getEndDate()).append("\n")
                               .append("Driver Needed: ").append(reserved.isDriver() ? "Yes" : "No").append("\n")
                               .append("Amount Paid: $").append(String.format("%.2f", paymentAmount)).append("\n");
             showAlert("Current Reservation", reservationDetails.toString());
         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Error", "An error occurred while fetching your reservations. Please try again later.");
         }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
