package model.vehicle;

import database.PersistenceHandler;
import database.PostgreSQL;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class VehicleAdapter {
	protected static PersistenceHandler persistenceHandler = PostgreSQL.getInstance();

	public void loadVehicles() {
		if (persistenceHandler.isVehiclesTableEmpty()) {
			List<Vehicle> vehicles = List.of(
					new Car(0, "Model X", "Model X", "Islamabad", "XYZ1234", 20000.00, "Reliable for city travel",
							"Car", true, 1),
					new Car(0, "Model Y", "Model Y", "Islamabad", "ABC5678", 35000.00,
							"Spacious and powerful for long trips", "Car", true, 1),
					new Car(0, "Model Z", "Model Z", "Islamabad", "LMN9876", 50000.00, "For thrill seekers", "Car",
							true, 1),
					new Car(0, "Model A", "Model A", "Islamabad", "DEF1234", 45000.00, "Eco-friendly and efficient",
							"Car", true, 1),
					new Car(0, "Model B", "Model B", "Islamabad", "GHI5678", 48000.00,
							"Sleek design with modern features", "Car", true, 1),
					new Bike(0, "Electric Scooter", "Electric Scooter", "Rawalpindi", "SCO1234", 1000.00,
							"Perfect for quick commutes", "Bike", true, 1),
					new Bike(0, "Scooter 3000", "Scooter 3000", "Rawalpindi", "SCO5678", 1500.00,
							"Convenient for short distances", "Bike", true, 1));

			// Save vehicles to the database
			for (Vehicle vehicle : vehicles) {
				int vehicleId = persistenceHandler.addVehicle1(vehicle);
				if (vehicleId > 0) {
					vehicle.setVehicleID(vehicleId);
					System.out.println("Vehicle ID set for " + vehicle.getModel() + ": " + vehicleId);
				} else {
					System.out.println("Failed to add vehicle: " + vehicle.getModel());
				}
			}
		} else {

			System.out.println("Vehicles already loaded in the database.");
		}
	}

	public String getImageUrll(String imagePath) {
		URL imageUrl = getClass().getResource(imagePath);
		if (imageUrl != null) {
			return imageUrl.toExternalForm();
		} else {
			System.out.println("Image not found: " + imagePath);
			return null;
		}
	}

	public List<Vehicle> getAllVehicles() throws SQLException {
		return persistenceHandler.getAllVehicles1(); // Fetch vehicles from DB
	}

	public boolean isVehicleAvailable(int vehicleId) throws SQLException {
		return persistenceHandler.isVehicleAvailable(vehicleId); // Check availability
	}

	public void updateVehicleAvailability(int vehicleId, boolean available) throws SQLException {
		persistenceHandler.updateVehicleAvailability(vehicleId, available); // Update availability status
	}

	public void addVehicle(Vehicle vehicle) throws SQLException {
		persistenceHandler.addVehicle(vehicle); // Add vehicle to DB
	}

	public boolean checkReservation(int renterID) throws SQLException {
		return persistenceHandler.checkMadeReservation(renterID);
	}

	public String gettingImageByVehicleId(int vehicleId) throws SQLException {
		return persistenceHandler.getImageByVehicleId(vehicleId);
	}

	public void setVehicleAvailable(int fetchedVehicleID) throws SQLException {
		persistenceHandler.setVehicleAvailable(fetchedVehicleID);
	}

	public String getVehicleNumberPlateByID(int fetchedVehicleID) throws SQLException {
		return persistenceHandler.getVehicleNumberPlateByID(fetchedVehicleID);
	}

	public int getVehicleIDByRegistrationPlate(String registrationPlate) throws SQLException {
		return persistenceHandler.getVehicleIDByRegistrationPlate(registrationPlate);
	}
}