package application.driver;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.reservation.Reservation;
import application.menu.DriverMenuController;

public class ViewScheduleController {
	
	private int driverID;
	
	@FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, Integer> reservationID;

    @FXML
    private TableColumn<Reservation, Integer> renterID;

    @FXML
    private TableColumn<Reservation, Integer> vehicleID;

    @FXML
    private TableColumn<Reservation, Integer> companyID;

    @FXML
    private TableColumn<Reservation, String> startDate;

    @FXML
    private TableColumn<Reservation, String> endDate;

    @FXML
    private TableColumn<Reservation, String> paymentStatus;
    
    @FXML
    private TableColumn<Reservation, String> additionalCharges;
    
    @FXML
    private Button exitButton;
    
    public void initialize()
    {
    	this.reservationID.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
    	this.renterID.setCellValueFactory(new PropertyValueFactory<>("renterID"));
    	this.vehicleID.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
    	this.companyID.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
    	this.startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
    	this.endDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    	this.paymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
    	this.additionalCharges.setCellValueFactory(new PropertyValueFactory<>("additionalCharges"));
    }
    
    public void setData(int driverID) {
        this.driverID = driverID;
        populateReservations();
    }

    private void populateReservations() {
        // Fetch data for the TableView
        Reservation reservation = new Reservation();
        ObservableList<Reservation> reservations = reservation.fetchReservationsForDriver(this.driverID);

        // Set the data to the TableView
        reservationTable.setItems(reservations);
    }
    
    @FXML
    private void handleExit(ActionEvent event)
    {
    	try {
            // Load the AdminMenu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/DriverMenu.fxml"));
            Parent root = loader.load();

            DriverMenuController obj = loader.getController();
            obj.setData(driverID);
            // Open the Admin Panel in a new stage
            Stage stage = new Stage();
            stage.setTitle("Driver Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (Manage Renter)
            ((Stage) exitButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
