package application.reservation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.reservation.Reservation;
import database.PostgreSQL;
import application.rentalco.*;
import application.vehicle.AddVehicleController;

import java.io.IOException;
import java.util.List;

public class ReservationGalleryController {

    @FXML
    private ScrollPane reservationScrollPane;
    @FXML
    private VBox reservationVBox;
    @FXML
    private Button addReservationButton;
    @FXML
    private Button assignDriverButton;
    @FXML
    private Button cancelButton;
    
    private int companyID;
    
    public void setCompanyID(int id) {
        this.companyID = id;
        loadReservations();
    }

    private void loadReservations() {
        // Fetch reservations from the database
        List<Reservation> reservations = PostgreSQL.getCompanyReservations(companyID);

        // Clear existing reservation cards
        reservationVBox.getChildren().clear();
        reservationVBox.setStyle("-fx-alignment: center; -fx-background-color: #000000; -fx-text-fill: white;");
        reservationVBox.setAlignment(Pos.BASELINE_CENTER);

        // Add a card for each reservation
        for (Reservation reservation : reservations) {
            HBox reservationCard = createReservationCard(reservation);
            reservationVBox.getChildren().add(reservationCard);
        }
    }

    private HBox createReservationCard(Reservation reservation) {
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: #70cd98; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: #000000; -fx-alignment: center; -fx-text-fill: white;");
        card.setAlignment(Pos.BASELINE_CENTER);

        // Add reservation details
        VBox detailsBox = new VBox(5);
        Text idText = new Text("Reservation ID: " + reservation.getReservationID());
        idText.setFill(Color.web("#f2fff7"));
        idText.setStyle("-fx-font-weight: bold;");
        Text renterText = new Text("Renter Username: " + PostgreSQL.getRenterUsername(reservation.getRenterID()));
        renterText.setFill(Color.web("#f2fff7"));
        Text vehicleText = new Text("Vehicle Plate Number: " + PostgreSQL.getVehicleNum(reservation.getVehicleID()));
        vehicleText.setFill(Color.web("#f2fff7"));
        String driverUsername = PostgreSQL.getDriverUsername(reservation.getDriverID());
        Text driverText = new Text("Driver Username: " + (driverUsername != null ? driverUsername : "None"));
        driverText.setFill(Color.web("#f2fff7"));
        Text startDateText = new Text("Start Date: " + reservation.getStartDate());
        startDateText.setFill(Color.web("#f2fff7"));
        Text endDateText = new Text("End Date: " + reservation.getEndDate());
        endDateText.setFill(Color.web("#f2fff7"));
        Text paymentText = new Text("Payment Status: " + (reservation.isPaymentStatus() ? "Paid" : "Unpaid"));
        paymentText.setFill(Color.web("#f2fff7"));
        Text chargesText = new Text("Additional Charges: $" + reservation.getAdditionalCharges());
        chargesText.setFill(Color.web("#f2fff7"));
        detailsBox.getChildren().addAll(idText, renterText, vehicleText, driverText, startDateText, endDateText, paymentText, chargesText);
        
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        // Add "Edit" and "Delete" buttons
        VBox buttonBox = new VBox(5);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        if (reservation.ifDriverNeeded() && driverUsername == null) {
            Button assignDriverButton = new Button("Assign Driver");
            assignDriverButton.setOnAction(event -> openAssignDriverForm(reservation));
            buttonBox.getChildren().add(assignDriverButton);
        }

        editButton.setOnAction(event -> openEditReservationForm(reservation));
        deleteButton.setOnAction(event -> openDeleteReservationForm(reservation));

        buttonBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(detailsBox, buttonBox);

        return card;
    }

    private void openEditReservationForm(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/reservation/EditReservation.fxml"));
            Parent editForm = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Edit Reservation");
            stage.setScene(new Scene(editForm));
            stage.show();

            EditReservationController controller = loader.getController();
            controller.setReservationToEdit(reservation);

            // Add listener to refresh list after editing
            stage.setOnHidden(event -> {
                loadReservations();
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the edit form.");
        }
    }
    
    private void openAssignDriverForm(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/rentalco/DriverGallery.fxml"));
            Parent driverForm = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Assign Driver");
            stage.setScene(new Scene(driverForm));
            stage.show();

            DriverGalleryController controller = loader.getController();
            controller.setReservation(reservation);
            controller.setCompanyID(companyID);
            
            stage.setOnHidden(event -> {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Driver assigned to Reservation " + reservation.getReservationID()+ " successfully!");
                loadReservations();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDeleteReservationForm(Reservation reservation) {
        // Create the confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Reservation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete reservation with ID " + reservation.getReservationID() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reservation.deleteReservation();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation with ID " +  reservation.getReservationID() + " deleted successfully!");
                loadReservations(); 
            } else {
                System.out.println("Deletion canceled for reservation with ID " + reservation.getReservationID());
            }
        });
    }

    @FXML
    private void showAddReservationForm() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/reservation/AddReservation.fxml"));
            Parent addReservationRoot = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Reservation");
            stage.setScene(new Scene(addReservationRoot));
            stage.show();
            
            AddReservationController controller = fxmlLoader.getController();
            controller.setCompanyID(companyID);
        } 
        catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the add reservation form.");
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
