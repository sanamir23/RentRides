package application.rentalco;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import database.PostgreSQL;
import model.user.Driver;
import model.reservation.Reservation;

import java.io.IOException;
import java.util.List;

public class DriverGalleryController {

    @FXML
    private ScrollPane driverScrollPane;
    @FXML
    private VBox driverVBox;
    @FXML
    private Button cancelButton;
    @FXML 
    private Button hireDriver;

    private Reservation currentReservation;
    
    private int companyID;
    
    public void setCompanyID(int id) {
    	this.companyID = id;
    	loadDrivers();
    }   

    public void setReservation(Reservation reservation) {
        this.currentReservation = reservation;
    }

    private void loadDrivers() {
        List<Driver> drivers = PostgreSQL.getCompanyDrivers(companyID);
        driverVBox.getChildren().clear();
        driverVBox.setStyle("-fx-alignment: center; -fx-background-color: #000000; -fx-text-fill: white;");
        driverVBox.setAlignment(Pos.BASELINE_CENTER);

        for (Driver driver : drivers) {
            HBox driverCard = createDriverCard(driver);
            driverVBox.getChildren().add(driverCard);
        }
    }

    private HBox createDriverCard(Driver driver) {
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: #70cd98; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: #000000; -fx-alignment: center; -fx-text-fill: white;");
        card.setAlignment(Pos.BASELINE_CENTER);

        VBox detailsBox = new VBox(5);
        Text nameText = new Text("Name: " + driver.getName());
        nameText.setFill(Color.web("#f2fff7"));
        Text licenseText = new Text("License: " + driver.getLicenseNum());
        licenseText.setFill(Color.web("#f2fff7"));
        Text ratingText = new Text("Phone: " + driver.getPhone());
        ratingText.setFill(Color.web("#f2fff7"));
        detailsBox.getChildren().addAll(nameText, licenseText, ratingText);

        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        Button chooseButton = new Button("Choose");
        chooseButton.setOnAction(event -> assignDriverToReservation(driver));

        VBox buttonBox = new VBox(5);
        buttonBox.getChildren().add(chooseButton);

        card.getChildren().addAll(detailsBox, buttonBox);

        return card;
    }

    private void assignDriverToReservation(Driver driver) {
        if (currentReservation == null) {
            showAlert("Error", "No reservation selected.", "Please select a reservation first.", Alert.AlertType.ERROR);
            return;
        }

        try {
            boolean isUpdated = PostgreSQL.assignDriverToReservation(currentReservation.getReservationID(), driver.getUserID());
            if (isUpdated) {
                showAlert("Success", "Driver Assigned!", "Driver " + driver.getName() + " has been assigned successfully.", Alert.AlertType.INFORMATION);
                PostgreSQL.updateDriverAvailability(driver.getUserID(), false);
                currentReservation.setDriverID(driver.getUserID());
                currentReservation.setIsDriver(false);
                boolean status = currentReservation.updateReservation();
                
                if (status) {
                	System.out.println("Reservation updated successfully!");
                }
                else {
                	System.out.println("Reservation update ERROR!");
                }
                // Close the form after successful assignment
                Stage stage = (Stage) driverScrollPane.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Error", "Driver Assignment Failed.", "Unable to assign the driver. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unexpected Error.", "An error occurred while assigning the driver.", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void showHireDriverForm() {
        try {
            // Load the AddVehicle.fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/rentalco/HireDrivers.fxml"));
            Parent hireDriverRoot = fxmlLoader.load();

            // Create a new Stage for the Add Vehicle form
            Stage stage = new Stage();
            stage.setTitle("Hire Driver");
            stage.setScene(new Scene(hireDriverRoot));
            stage.show();
            
            HireDriversController controller = fxmlLoader.getController();
            controller.setCompanyID(companyID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
