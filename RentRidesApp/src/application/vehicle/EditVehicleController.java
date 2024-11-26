package application.vehicle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.vehicle.Vehicle;
import java.io.File;

public class EditVehicleController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField registrationNumField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField rentField;

    @FXML
    private TextArea featuresArea;

    @FXML
    private ChoiceBox<String> vehicleTypeChoiceBox;

    @FXML
    private CheckBox availabilityCheckBox;

    @FXML
    private ImageView imageView;

    @FXML
    private Button addImageButton;

    @FXML
    private Button removeImageButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Vehicle vehicleToEdit;
    private Image vehicleImage;

    @FXML
    private void initialize() {
        vehicleTypeChoiceBox.getItems().addAll("Car", "Bike");

        // Handle adding a new image
        addImageButton.setOnAction(event -> addImage());

        // Handle removing the current image
        removeImageButton.setOnAction(event -> removeImage());

        // Handle save button
        saveButton.setOnAction(event -> saveChanges());

        // Handle cancel button
        cancelButton.setOnAction(event -> cancelEdit());
    }

    public void setVehicleToEdit(Vehicle vehicle) {
        this.vehicleToEdit = vehicle;

        // Populate fields with vehicle details
        nameField.setText(vehicle.getName());
        modelField.setText(vehicle.getModel());
        registrationNumField.setText(vehicle.getRegistrationNum());
        locationField.setText(vehicle.getLocation());
        rentField.setText(String.valueOf(vehicle.getRentPerDay()));
        featuresArea.setText(vehicle.getFeatures());
        vehicleTypeChoiceBox.setValue(vehicle.getVehicleType());
        availabilityCheckBox.setSelected(vehicle.isAvailable());

        // Load the existing image, if available
        if (vehicle.getImageUrl() != null && !vehicle.getImageUrl().isEmpty()) {
            vehicleImage = new Image(vehicle.getImageUrl());
            imageView.setImage(vehicleImage);
        }
    }

    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            vehicleImage = new Image(selectedFile.toURI().toString());
            imageView.setImage(vehicleImage);
            showAlert(Alert.AlertType.INFORMATION, "Image Selection Sucessful", vehicleImage.getUrl() + " was selected.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Image Selection Failed", "No image was selected.");
        }
    }

    private void removeImage() {
        if (vehicleImage != null) {
            vehicleImage = null;
            imageView.setImage(null);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Image to Remove", "No image is currently associated with this vehicle.");
        }
    }

    private void saveChanges() {
        try {
            vehicleToEdit.setName(nameField.getText());
            vehicleToEdit.setModel(modelField.getText());
            vehicleToEdit.setRegistrationNum(registrationNumField.getText());
            vehicleToEdit.setLocation(locationField.getText());
            vehicleToEdit.setRentPerDay(Double.parseDouble(rentField.getText()));
            vehicleToEdit.setFeatures(featuresArea.getText());
            vehicleToEdit.setVehicleType(vehicleTypeChoiceBox.getValue());
            vehicleToEdit.setAvailable(availabilityCheckBox.isSelected());

            // Save the image URL
            if (vehicleImage != null) {
                vehicleToEdit.setImageUrl(vehicleImage.getUrl());
            } else {
                vehicleToEdit.setImageUrl(null); // Clear the image if removed
            }

            // Save the updated vehicle to the database
            if (vehicleToEdit.updateVehicle()) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle updated successfully!");
                cancelEdit();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update the vehicle.");
                cancelEdit();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please ensure all fields are filled correctly.");
        }
    }
    
    @FXML
    private void cancelEdit() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
