package application.renter;

import application.menu.RenterMenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.vehicle.Vehicle;
import model.vehicle.VehicleAdapter;
import model.user.User;

public class VehicleDetailsController {

    @FXML
    private TextArea detailsTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button rentButton;
    @FXML
    private Button backButton;
    @FXML
    private VBox vehicleListVBox;

    private Vehicle vehicle;
    private User user;
    private VehicleAdapter vehicleService = new VehicleAdapter();

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        String details = "ID: " + vehicle.getId() +
                "\nModel: " + vehicle.getModel() +
                "\nLocation: " + vehicle.getLocation() +
                "\nPrice: $" + vehicle.getPrice() +
                "\nDescription: " + vehicle.getDescription();
        detailsTextArea.setText(details);
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void initialize() {
        saveButton.setOnAction(e -> saveVehicle());
        rentButton.setOnAction(e -> rentVehicle());
        backButton.setOnAction(e -> handleBackAction());
    }

    private void saveVehicle() {
        try {
            vehicleService.addVehicle(vehicle); // Assuming addVehicle saves the vehicle
            showAlert("Saved", "This vehicle has been saved for later.");
        } catch (Exception e) {
            showAlert("Database Error", "Failed to save vehicle.");
        }
    }

    private void rentVehicle() {
        try {
            if (!vehicleService.isVehicleAvailable(vehicle.getId())) {
                showAlert("Vehicle Not Available", "The selected vehicle is not available for the chosen dates.");
                return;
            }

            boolean hasActiveReservation = vehicleService.checkReservation(user.getUserID());
            if (hasActiveReservation) {
                showAlert("Reservation Not Allowed", "You already have an active reservation. Please complete or cancel your current reservation before making a new one.");
                return;
            }

            //vehicleListVBox.setVisible(false);
            vehicleService.updateVehicleAvailability(vehicle.getId(), false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/MakeReservation.fxml"));
            Parent reservationRoot = loader.load();

            MakeReservationController rc = loader.getController();
            rc.setSelectedVehicleID(vehicle.getId());
            rc.setSelectedRenter(user);
            rc.setSelectedVehicle(vehicle);

            Stage reservationStage = new Stage();
            reservationStage.setTitle("Make Reservation");
            Scene scene = new Scene(reservationRoot);
            reservationStage.setScene(scene);
            // Close the current window (stage) before opening the new one
            Stage currentStage = (Stage) rentButton.getScene().getWindow();
            currentStage.close(); // Close the current window
            reservationStage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to rent the vehicle.");
            e.printStackTrace();
        }
    }

   /* private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }*/
 
	
    public void handleBackAction() {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/BrowseVehicles.fxml"));
			Parent root = loader.load();
			BrowseVehiclesController controller = loader.getController();
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
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
