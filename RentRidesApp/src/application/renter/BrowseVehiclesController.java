package application.renter;

import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.vehicle.Vehicle;
import model.vehicle.VehicleAdapter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.menu.DriverMenuController;
import application.menu.RenterMenuController;

public class BrowseVehiclesController {
	@FXML
	private VBox vehicleListVBox;
	@FXML
	private TextField locationFilter, minPriceFilter, maxPriceFilter, modelFilter;
	@FXML
	private ComboBox<String> typeFilter;
	@FXML
	private VBox vehicleListContainer;
	@FXML
	private Label noResultsLabel;
	@FXML
	private Button showVehiclesButton;
	@FXML
	private ImageView vehicleImageView;
	@FXML
	private Button backButton;
	private final List<Vehicle> vehicls = new ArrayList<>();

	private VehicleAdapter vehicleService = new VehicleAdapter(); // Use VehicleService

	@FXML
	private Button rentButton;

	private User user;

	@FXML
	public void initialize() {
		vehicleService.loadVehicles();
		loadVehiclesFromDatabase();
		typeFilter.getItems().addAll("All", "Car", "Bike");
		typeFilter.getSelectionModel().select("All");
		displayVehicles(vehicls);
		vehicleListContainer.getChildren().clear();
		noResultsLabel.setVisible(false);
		displayVehicles(vehicls);
	}

	private void loadVehiclesFromDatabase() {
		try {
			vehicls.clear();
			vehicls.addAll(vehicleService.getAllVehicles());

		} catch (SQLException e) {
			showAlert("Database Error", "Failed to load vehicles from the database.");
		}
	}

	public void setRenter(User renter) {
		user = renter;
	}

	public int getRenterId() {
		return user.getUserID();
	}

	public void displayVehicleImage() {
		String imagePath = "/images/car.jpg";

		URL imageUrl = getClass().getResource(imagePath);

		if (imageUrl != null) {
			Image image = new Image(imageUrl.toExternalForm());

			vehicleImageView.setImage(image);
		} else {
			System.out.println("Image not found: " + imagePath);
		}
	}

	@FXML
	private void showAvailableVehicles() {
		displayVehicles(vehicls);
		// showVehiclesButton.setVisible(false);
	}

	private void displayVehicles(List<Vehicle> vehiclesToDisplay) {
		vehicleListContainer.getChildren().clear();

		if (vehiclesToDisplay.isEmpty()) {
			noResultsLabel.setText("No vehicles available.");
			noResultsLabel.setVisible(true);
			vehicleListContainer.getChildren().add(noResultsLabel);
			return;
		}

		noResultsLabel.setVisible(false);
		for (Vehicle vehicle : vehiclesToDisplay) {
			try {
				System.out.print(vehicle.getId());
				String imageUrl = vehicleService.getImageUrll(vehicleService.gettingImageByVehicleId(vehicle.getId()));
				System.out.print(imageUrl);
				ImageView imageView;
				try {
					if (imageUrl != null && !imageUrl.trim().isEmpty()) {
						System.out.println("Image URL: " + imageUrl);
						Image image = new Image(imageUrl, true); // 'true' enables background loading
						imageView = new ImageView(image);
					} else {
						throw new IllegalArgumentException("Invalid or null image URL.");
					}
				} catch (Exception e) {
					// Handle exceptions and use a default image as a fallback
					System.err.println("Error loading image: " + e.getMessage());
					imageView = new ImageView(new Image("file:/C:/Users/Sana%20Mir/Desktop/SDA_Project/RentRidesApp/resources/vehicle_images/civic.jpg")); // Default image if no URL is available
				}

				imageView.setFitHeight(100);
				imageView.setFitWidth(150);
				imageView.setPreserveRatio(true);

				Label idLabel = new Label(String.valueOf(vehicle.getId()));
				Label descriptionLabel = new Label(vehicle.getFeatures());
				Label priceLabel = new Label("Price: $" + vehicle.getPrice());
				Label locationLabel = new Label("Location: " + vehicle.getLocation());

				Button viewDetailsButton = new Button("View Details");
				viewDetailsButton.setOnAction(e -> showVehicleDetails(vehicle));

				VBox itemBox = new VBox(10, imageView, idLabel, descriptionLabel, priceLabel, locationLabel,
						viewDetailsButton);
				itemBox.setStyle("-fx-alignment: center;");

				vehicleListContainer.getChildren().add(itemBox);
			} catch (SQLException e) {
				showAlert("Database Error", "Failed to load vehicle image.");
			}
		}
	}

	private void showVehicleDetails(Vehicle vehicle) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/renter/VehicleDetails.fxml"));
			Parent root = loader.load();
			VehicleDetailsController controller = loader.getController();
			controller.setVehicle(vehicle);
			controller.setUser(user);

			Stage detailsStage = new Stage();
			detailsStage.setTitle(vehicle.getModel() + " Details");
			detailsStage.setScene(new Scene(root));
			detailsStage.show();
			Stage currentStage = (Stage) vehicleListContainer.getScene().getWindow();
			currentStage.close();

		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Failed to load vehicle details.");
		}
	}

	private void saveVehicle(Vehicle vehicle) {
		try {
			vehicleService.addVehicle(vehicle);
			vehicls.add(vehicle);
			showAlert("Saved", "This vehicle has been saved for later.");
		} catch (SQLException e) {
			showAlert("Database Error", "Failed to save vehicle.");
		}
	}

	@FXML
	private void searchVehicles() {
		String location = locationFilter.getText().trim();
		String model = modelFilter.getText().trim();
		String type = typeFilter.getSelectionModel().getSelectedItem();
		double minPrice = parsePrice(minPriceFilter.getText(), 0);
		double maxPrice = parsePrice(maxPriceFilter.getText(), Double.MAX_VALUE);

		List<Vehicle> filteredVehicles = filterVehicles(location, model, minPrice, maxPrice, type);

		if (filteredVehicles.isEmpty()) {
			noResultsLabel.setText("No vehicles found matching your search criteria.");
			noResultsLabel.setVisible(true);
			vehicleListContainer.getChildren().clear();
			vehicleListContainer.getChildren().add(noResultsLabel);
		} else {
			noResultsLabel.setVisible(false);
			displayVehicles(filteredVehicles);
		}
	}

	private double parsePrice(String input, double defaultValue) {
		try {
			return input.isEmpty() ? defaultValue : Double.parseDouble(input);
		} catch (NumberFormatException e) {
			showAlert("Invalid Input", "Please enter a valid price.");
			return defaultValue;
		}
	}

	private List<Vehicle> filterVehicles(String location, String model, double minPrice, double maxPrice, String type) {
		List<Vehicle> filteredVehicles = new ArrayList<>();

		for (Vehicle vehicle : vehicls) {
			boolean matchesLocation = location.isEmpty() || vehicle.getLocation().equalsIgnoreCase(location);
			boolean matchesModel = model.isEmpty() || vehicle.getModel().toLowerCase().contains(model.toLowerCase());
			boolean matchesType = type.equals("All") || vehicle.getClass().getSimpleName().equalsIgnoreCase(type);
			boolean matchesPrice = vehicle.getPrice() >= minPrice && vehicle.getPrice() <= maxPrice;

			if (matchesLocation && matchesModel && matchesType && matchesPrice) {
				filteredVehicles.add(vehicle);
			}
		}

		return filteredVehicles;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML

	public void handleBackAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/RenterMenu.fxml"));
			Parent root = loader.load();
			RenterMenuController controller = loader.getController();
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
}
