package application.vehicle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.*;
import model.vehicle.Vehicle;
import database.PostgreSQL;

import java.io.IOException;
import java.util.List;

public class VehicleGalleryController {

    @FXML
    private ScrollPane vehicleScrollPane;
    @FXML
    private VBox vehicleVBox;    
    @FXML
    private Button addVehicleButton;
    @FXML
    private Button cancelButton;
    
    private int companyID;
    
    public void setCompanyID(int id) {
    	this.companyID = id;
    	loadVehicles();
    }

    private void loadVehicles() {
        // Fetch vehicles from the database
        List<Vehicle> vehicles = PostgreSQL.getCompanyVehicles(companyID);

        // Clear existing vehicle cards
        vehicleVBox.getChildren().clear();
        vehicleVBox.setStyle("-fx-alignment: center; -fx-background-color: #000000; -fx-text-fill: white;");
        vehicleVBox.setAlignment(Pos.BASELINE_CENTER);

        // Add a card for each vehicle
        for (Vehicle vehicle : vehicles) {
            HBox vehicleCard = createVehicleCard(vehicle);
            vehicleVBox.getChildren().add(vehicleCard);
        }
    }

    private HBox createVehicleCard(Vehicle vehicle) {
        // Create a container for the card
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: #70cd98; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: #000000; -fx-alignment: center; -fx-text-fill: white;");
        card.setAlignment(Pos.BASELINE_CENTER);

        // Add the vehicle image
        ImageView imageView = new ImageView();
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            imageView.setImage(new Image(vehicle.getImageUrl())); // Load the image from the URL
        } else {
            imageView.setImage(new Image("file:/C:/Users/Sana%20Mir/Desktop/SDA_Project/RentRidesApp/resources/vehicle_images/civic.jpg")); // Default image if no URL is available
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(100);

        // Add vehicle details
        VBox detailsBox = new VBox(5);
        Text nameText = new Text("Name: " + vehicle.getName());
        nameText.setFill(Color.web("#f2fff7"));
        Text modelText = new Text("Model: " + vehicle.getModel());
        modelText.setFill(Color.web("#f2fff7"));
        Text rentText = new Text("Rent/Day: $" + vehicle.getRentPerDay());
        rentText.setFill(Color.web("#f2fff7"));
        rentText.setStyle("-fx-font-weight: bold;");
        detailsBox.getChildren().addAll(nameText, modelText, rentText);
        detailsBox.setAlignment(Pos.CENTER);

        // Expand the detailsBox to take remaining space
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        // Add "Edit" and "Delete" buttons
        VBox buttonBox = new VBox(5);
        buttonBox.setAlignment(Pos.CENTER);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        // Set button actions
        editButton.setOnAction(event -> openEditVehicleForm(vehicle));
        deleteButton.setOnAction(event -> openDeleteVehicleForm(vehicle));

        buttonBox.getChildren().addAll(editButton, deleteButton);

        // Add elements to the card
        card.getChildren().addAll(imageView, detailsBox, buttonBox);

        return card;
    }

    private void openEditVehicleForm(Vehicle v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/vehicle/EditVehicle.fxml"));
            Parent editForm = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Edit Vehicle");
            stage.setScene(new Scene(editForm));

            // Close event to refresh the vehicle list
            stage.setOnHiding(event -> loadVehicles());
            stage.show();

            EditVehicleController controller = loader.getController();
            controller.setVehicleToEdit(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDeleteVehicleForm(Vehicle vehicle) {
        // Show a confirmation alert to the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Vehicle");
        alert.setHeaderText("Are you sure you want to delete this vehicle?");
        alert.setContentText("Vehicle ID: " + vehicle.getId());

        // Show the dialog and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Attempt to delete the vehicle
                boolean deleted = vehicle.deleteVehicle();
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle deleted successfully.");
                    loadVehicles(); // Reload the vehicle list after deletion
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Failed to delete the vehicle. Please try again.");
                }
            }
        });
    }
    
    @FXML
    private void showAddVehicleForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/vehicle/AddVehicle.fxml"));
            Parent addVehicleRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Vehicle");
            stage.setScene(new Scene(addVehicleRoot));

            // Close event to refresh the vehicle list
            stage.setOnHiding(event -> loadVehicles());
            stage.show();

            AddVehicleController controller = loader.getController();
            controller.setCompanyID(companyID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
