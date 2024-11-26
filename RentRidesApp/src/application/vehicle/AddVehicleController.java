package application.vehicle;

import model.vehicle.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddVehicleController {
    @FXML
    private TextField imageInputField;
    @FXML
    private Button addImageButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField registrationNumField;
    @FXML
    private TextField rentPerDayField;
    @FXML
    private TextArea featuresArea;
    @FXML
    private ChoiceBox<String> vehicleTypeChoiceBox;
    @FXML
    private TextField locationField;
    @FXML
    private Label fileNameLabel;

    private String imageUrl; // Holds the single image URL
    
    private int companyID;
    
    public void setCompanyID(int id) {
        this.companyID = id;
    }

    @FXML
    private void initialize() {
        vehicleTypeChoiceBox.getItems().addAll("Car", "Bike");
        fileNameLabel.setText("No file selected");
    }

    @FXML
    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Set the image URL to the selected file's URI
            imageUrl = selectedFile.toURI().toString();
            
            // Update the file name label
            fileNameLabel.setText("Selected File: " + selectedFile.getName());
            
            // Disable the "Add Image" button
            addImageButton.setDisable(true);

            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Success", "Image added successfully!");
        } else {
            // Show error alert if no file was selected
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select an image.");
        }
    }

    @FXML
    private void saveVehicle() {
        // Retrieve user input
        String name = nameField.getText().trim();
        String model = modelField.getText().trim();
        String registrationNum = registrationNumField.getText().trim();
        String rentPerDayText = rentPerDayField.getText().trim();
        String features = featuresArea.getText().trim();
        String vehicleType = vehicleTypeChoiceBox.getValue();
        String location = locationField.getText().trim();

        // Basic empty fields validation
        if (name.isEmpty() || model.isEmpty() || registrationNum.isEmpty() ||
                rentPerDayText.isEmpty() || vehicleType == null || features.isEmpty() || location.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled in correctly.");
            return;
        }

        // Validate rentPerDay is a valid positive number
        double rentPerDay;
        try {
            rentPerDay = Double.parseDouble(rentPerDayText);
            if (rentPerDay <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Rent per day must be a positive numeric value.");
            return;
        }

        // Validate registration number format
        if (!registrationNum.matches("^[A-Z]{3}-\\d{4}$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration number must be in the format ABC-1234.");
            return;
        }

        // Check if an image has been added
        if (imageUrl == null || imageUrl.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please add an image for the vehicle.");
            return;
        }

        // Create a new vehicle object based on the selected vehicle type
        Vehicle vehicle;
        if (vehicleType.equals("Car")) {
            vehicle = new Car(name, registrationNum, model, rentPerDay, location, features, true, companyID);
        } else if (vehicleType.equals("Bike")) {
            vehicle = new Bike(name, registrationNum, model, rentPerDay, location, features, true, companyID);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid vehicle type selected.");
            return;
        }

        // Set the single image URL for the vehicle
        vehicle.setImageUrl(imageUrl);

        // Save the vehicle using the saveVehicle method
        boolean isSaved = vehicle.saveVehicle();
        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle saved successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the vehicle.");
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
    private void exit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
