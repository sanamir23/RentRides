package application.rentalco;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import database.PostgreSQL;
import model.user.Driver;
import javafx.geometry.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class CompanyDriversController {

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

    public void loadDrivers() {
    	System.out.println(companyID);
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
        Text phoneText = new Text("Phone: " + driver.getPhone());
        phoneText.setFill(Color.web("#f2fff7"));
        detailsBox.getChildren().addAll(nameText, licenseText, phoneText);

        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> openEditForm(driver));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteDriver(driver));

        VBox buttonBox = new VBox(5);
        buttonBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(detailsBox, buttonBox);

        return card;
    }

    private void openEditForm(Driver driver) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/rentalco/EditDriver.fxml"));
            Parent root = loader.load();

            EditDriverController controller = loader.getController();
            controller.setDriver(driver, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Driver");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Unable to load form", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteDriver(Driver driver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Driver");
        alert.setContentText("Are you sure you want to delete driver: " + driver.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                PostgreSQL.deleteDriver(driver.getUserID());
                loadDrivers();
            }
        });
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
