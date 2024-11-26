package application.renter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
//import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.reservation.Reservation;
import model.user.Renter;
import model.user.User;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.VBox;
import model.vehicle.Vehicle;
import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.sql.SQLException;
//import database.PersistenceHandler;
//import database.PostgreSQL;

public class MakeReservationController {

    @FXML
    private DatePicker startDateField;
    @FXML
    private DatePicker endDateField;
    @FXML
    private CheckBox driverNeededField;
    @FXML 
    private Button submitButton;
    private int selectedVehicleID;  
    private int selectedRenterId;
    User user;
    private Vehicle vehicle;
    private Reservation reservation;
    
    boolean isDriverNeeded = false;

    public void setSelectedVehicleID(int vehicleID) {
    	
        this.selectedVehicleID = vehicleID;
    }
    public void setSelectedVehicle(Vehicle vehicle) {
    	this.vehicle=vehicle;
    }
    public void setSelectedRenter(User renter) {
        user=renter;
    }
    public void setSelectedRenterID(int renterId) {
        this.selectedRenterId = renterId;
    }
    

    @FXML
    private void handleReserveButton() throws SQLException {
        
    	Renter renter=(Renter) user;
        int renterID = user.getUserID();
        
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();
       
        isDriverNeeded = driverNeededField.isSelected();
        System.out.print(isDriverNeeded);
        driverNeededField.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { 
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Driver Selection");
                alert.setHeaderText(null);
                alert.setContentText("A driver has been selected.");
                alert.showAndWait();
            }
        });

        submitButton.setOnAction(e -> {
            try {
            	System.out.print(selectedVehicleID);
            	if(isDriverNeeded) {
                reservation = new Reservation(0, renterID, selectedVehicleID,14,26, startDate, endDate, false, 0.0, isDriverNeeded);
                reservation.setStartDate(startDate);
                reservation.setEndDate(endDate);
                reservation.setDriverID(1);
            	}
            	else
            	{
        		 reservation = new Reservation(0, renterID, selectedVehicleID,14,-1, startDate, endDate, false, 0.0, false);
                 reservation.setStartDate(startDate);
                 reservation.setEndDate(endDate);
                 reservation.setDriverID(-1);
            	}
             	reservation.updateMadeReservationFlag(renterID, true);

             	renter.setMadeReservation(true);
	            System.out.print("printing dates in reservation controller: "+reservation.getStartDate()+" "+reservation.getEndDate()+"\n");

                int reservationId = reservation.saveReservationInTable(reservation);
                reservation.setReservationID(reservationId);
                if(isDriverNeeded==true) {
                	reservation.updateDriverNeedStatus(reservationId, true);
                }
                showAlert("Reservation Successful", "Your reservation has been created successfully.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/ProcessPayment.fxml"));
	            AnchorPane paymentRoot = loader.load();
	            System.out.print("printing reservationid in reservation controller: "+reservation.getReservationID());
	            
	            ProcessPaymentController rc = loader.getController();
	        	rc.setReservation(reservation);
	        	rc.setVehicle(vehicle);
	        	rc.setSelectedRenter(user);
	        	
	        	System.out.println("Setting Start Date in PaymentController: " + reservation.getStartDate());
	        	System.out.println("Setting End Date in PaymentController: " + reservation.getEndDate());

	        	
	            Stage paymentStage = new Stage();
	            paymentStage.setTitle("Make Payment");
	            paymentStage.setScene(new Scene(paymentRoot));
	            paymentStage.show();
                
            } catch (Exception e1) {
            	System.out.print("error");
                e1.printStackTrace();
            }
            
        });
        
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

