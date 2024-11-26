package application.rentalco;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import model.user.Driver;
import database.PostgreSQL;

import java.util.List;

public class HireDriversController {

    @FXML
    private ScrollPane driverScrollPane;
    @FXML
    private VBox driverVBox;
    @FXML
    private Button cancelButton;
    
    private int companyID;
    
    public void setCompanyID(int id) {
    	this.companyID = id;
    	loadDrivers();
    }

    private void loadDrivers() {
        // Fetch drivers from the database
        List<Driver> drivers = PostgreSQL.getPendingDrivers(companyID);
        driverVBox.getChildren().clear();
        driverVBox.setStyle("-fx-alignment: top_center; -fx-background-color: #000000; -fx-text-fill: white;");
        driverVBox.setAlignment(Pos.BASELINE_CENTER);

        // Add a card for each driver
        for (Driver driver : drivers) {
            HBox driverCard = createDriverCard(driver);
            driverVBox.getChildren().add(driverCard);
        }
    }

    private HBox createDriverCard(Driver driver) {
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: #70cd98; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: #000000; -fx-alignment: top_center; -fx-text-fill: white;");
        card.setAlignment(Pos.BASELINE_CENTER);

        // Add driver details
        VBox detailsBox = new VBox(5);
        Text nameText = new Text("Name: " + driver.getName());
        nameText.setFill(Color.web("#f2fff7"));
        Text emailText = new Text("Email: " + driver.getEmail());
        emailText.setFill(Color.web("#f2fff7"));
        Text phoneText = new Text("Phone: " + driver.getPhone());
        phoneText.setFill(Color.web("#f2fff7"));
        Text addressText = new Text("Address: " + driver.getAddress());
        addressText.setFill(Color.web("#f2fff7"));
        Text licenseText = new Text("License Number: " + driver.getLicenseNum());
        licenseText.setFill(Color.web("#f2fff7"));
        detailsBox.getChildren().addAll(nameText, emailText, phoneText, addressText, licenseText);

        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        // Add "Accept" and "Reject" buttons
        VBox buttonBox = new VBox(5);
        Button acceptButton = new Button("Accept");
        Button rejectButton = new Button("Reject");

        // Set button actions
        acceptButton.setOnAction(event -> acceptDriver(driver));
        rejectButton.setOnAction(event -> rejectDriver(driver));

        buttonBox.getChildren().addAll(acceptButton, rejectButton);

        // Add elements to the card
        card.getChildren().addAll(detailsBox, buttonBox);

        return card;
    }

    private void acceptDriver(Driver driver) {
        PostgreSQL.updateDriverStatus(driver.getUserID(), "Accepted"); 
        loadDrivers();
    }

    private void rejectDriver(Driver driver) {
        // Delete the driver from the database
        PostgreSQL.deleteDriver(driver.getUserID());

        // Refresh the driver list
        loadDrivers();
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
