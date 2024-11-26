package application.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

import application.rentalco.CompanyDriversController;
import application.rentalco.FeedbackController;
import application.vehicle.VehicleGalleryController;
import application.reservation.ReservationGalleryController;

public class RentalCoMenuController {

    @FXML
    private Button showAllVehiclesButton;

    @FXML
    private Button showActiveReservationsButton;

    @FXML
    private Button showCompanyDriversButton;
    
    @FXML 
    private Button showFeedbackButton;

    @FXML
    private Button exitButton;
    
    private int companyID;
    
    public void setData(int id) {
        this.companyID = id;
    }
    
    @FXML
    private void showAllVehiclesPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/vehicle/VehicleGallery.fxml"));
            Parent showVehiclesRoot = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Your Vehicles");
            stage.setScene(new Scene(showVehiclesRoot));
            stage.show();
            
            System.out.println(companyID);
            VehicleGalleryController controller = fxmlLoader.getController();
            controller.setCompanyID(this.companyID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showAllReservationsPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/reservation/ReservationGallery.fxml"));
            Parent showReservationsRoot = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Your Reservations");
            stage.setScene(new Scene(showReservationsRoot));
            stage.show();
            
            System.out.println(companyID);
            ReservationGalleryController controller = fxmlLoader.getController();
            controller.setCompanyID(this.companyID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showAllDriversPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/rentalco/CompanyDrivers.fxml"));
            Parent showDriversRoot = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Your Drivers");
            stage.setScene(new Scene(showDriversRoot));
            stage.show();
            
            System.out.println(companyID);
            CompanyDriversController controller = fxmlLoader.getController();
            controller.setCompanyID(this.companyID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showFeedbackPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/rentalco/FeedbackGallery.fxml"));
            Parent showFeedbackRoot = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Your Feedback");
            stage.setScene(new Scene(showFeedbackRoot));
            stage.show();
            
            System.out.println(companyID);
            FeedbackController controller = fxmlLoader.getController();
            controller.setCompanyID(this.companyID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void exit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
