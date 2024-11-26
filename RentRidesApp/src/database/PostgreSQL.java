package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.reservation.Feedback;
import model.reservation.Reservation;
import model.vehicle.Bike;
import model.vehicle.Car;
import model.vehicle.Vehicle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.user.User;
import model.user.Driver;

public class PostgreSQL implements PersistenceHandler {

	private static PostgreSQL instance = null; // for singleton pattern

	private static final String url = "jdbc:postgresql://localhost:5432/rent_rides";
	private static final String username = "postgres";
	private static final String password = "killercode23";
	private static final String jdbc_driver = "org.postgresql.Driver";
	private Connection connection;
	private boolean isConnected;

	private PostgreSQL() {
		isConnected = false;
		connection = null;
		this.connect();
	}

	// Public method to provide access to the single instance
	public static synchronized PostgreSQL getInstance() {
		if (instance == null) {
			instance = new PostgreSQL();
		}
		return instance;
	}

	private void connect() {
		try {
			Class.forName(jdbc_driver);

			System.out.println("Connecting to database...");
			this.connection = DriverManager.getConnection(url, username, password);

			if (connection != null) {
				isConnected = true;
				System.out.println("Connection successful!");
			} else {
				System.out.println("Failed to make connection!");
			}

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateMadeReservation(int renterID, boolean madeReservation) {
		String query = "UPDATE Renter SET madeReservation = ? WHERE renterID = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setBoolean(1, madeReservation);
			stmt.setInt(2, renterID);
			int rowsUpdated = stmt.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Renter's madeReservation status updated successfully.");
			} else {
				System.out.println("No renter found with ID: " + renterID);
			}
		} catch (SQLException e) {
			System.out.println("Failed to update madeReservation: " + e.getMessage());
		}
	}

	@Override
	public boolean checkMadeReservation(int renterID) {
		String query = "SELECT madeReservation FROM Renter WHERE renterID = ?";
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setInt(1, renterID);
			try (ResultSet rs = stmnt.executeQuery()) {
				if (rs.next()) {
					return rs.getBoolean("madeReservation");
				}
			}
		} catch (SQLException e) {
			System.out.println("Failed to update madeReservation: " + e.getMessage());
		}
		return false;
	}

	/*
	 * @Override public Reservation getActiveReservationByRenterID(int renterID) {
	 * String query = "SELECT * FROM Reservation WHERE renterID = ?"; try
	 * (PreparedStatement stmt = connection.prepareStatement(query)) {
	 * stmt.setInt(1, renterID); ResultSet rs = stmt.executeQuery(); if (rs.next())
	 * { return new Reservation(rs.getInt("reservationID"), rs.getInt("renterID"),
	 * rs.getInt("vehicleID"), rs.getInt("companyID"), rs.getInt("driverID"),
	 * rs.getDate("startDate").toLocalDate(), rs.getDate("endDate").toLocalDate(),
	 * rs.getBoolean("driverNeed"), rs.getBoolean("paymentStatus"),
	 * rs.getDouble("additionalCharges")); } } catch (SQLException e) {
	 * System.out.println("Failed to update madeReservation: " + e.getMessage()); }
	 * return null; // Return null if no active reservation is found }
	 */
	@Override
	public Reservation getActiveReservationByRenterID(int renterID) {
		String query = "SELECT * FROM Reservation WHERE renterID = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, renterID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Reservation(rs.getInt("reservationID"), rs.getInt("renterID"), rs.getInt("vehicleID"),
						rs.getInt("companyID"), rs.getInt("driverID"), rs.getDate("startDate").toLocalDate(),
						rs.getDate("endDate").toLocalDate(), rs.getBoolean("paymentStatus"),
						rs.getDouble("additionalCharges"), rs.getBoolean("isDriver"));
			}
		} catch (SQLException e) {
			System.out.println("Failed to update madeReservation: " + e.getMessage());
		}
		return null; // Return null if no active reservation is found
	}

	@Override
	public double getPaymentAmountByReservationID(int reservationID) {
		String query = "SELECT amount FROM Payments WHERE reservation_id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, reservationID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getDouble("amount");
			}
		} catch (SQLException se) {
			se.printStackTrace();
			return 0.0; // Default to 0.0 if no payment record is found
		}
		return 0.0;
	}

	@Override
	public void updateReservationInDatabase(int reservationID, int renterID, double additional_price) {
		String query = "UPDATE reservation SET additionalCharges=? WHERE reservationID = ? AND renterID = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {

			// Set the parameters for the update query
			stmt.setDouble(1, additional_price); // Add the additional price to the current payment amount
			stmt.setInt(2, reservationID); // The reservation ID
			stmt.setInt(3, renterID); // The renter ID

			// Execute the update
			int rowsUpdated = stmt.executeUpdate();

			// Check if the update was successful
			if (rowsUpdated > 0) {
				System.out.println("Reservation updated successfully.");
			} else {
				System.out.println("No reservation found with the given ID and Renter ID.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override

	public int getVehicleIDByRegistrationPlate(String registrationPlate) {

		String query = "SELECT id FROM Vehicle WHERE registration_number = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, registrationPlate);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("id"); // Return the vehicleID
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return 0;
	}

	@Override
	public String getVehicleNameByID(int vehicleID) {
		String vehicleName = null;
		String query = "SELECT name FROM Vehicle WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			// Set the vehicle ID parameter
			ps.setInt(1, vehicleID);

			// Execute the query
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					vehicleName = rs.getString("name");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vehicleName; // Returns the vehicle name or null if not found
	}

	@Override
	public String getRenterNameByID(int renterID) {
		String renterName = null;
		String query = "SELECT renterName FROM Renter WHERE renterID = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			// Set the renter ID parameter
			ps.setInt(1, renterID);

			// Execute the query
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					renterName = rs.getString("renterName");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return renterName; // Returns the renter's name or null if not found
	}

	@Override
	public void setVehicleAvailable(int vehicleID) {
		String updateQuery = "UPDATE Vehicle SET isAvailable = true WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
			stmt.setInt(1, vehicleID);
			int rowsUpdated = stmt.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Vehicle availability updated successfully.");
			} else {
				System.out.println("No vehicle found with the given vehicleID.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getRenterIDFromReservation(int reservationID) {
		String query = "SELECT renterID FROM reservation WHERE reservationID = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, reservationID);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("renterID");
			} else {
				throw new SQLException("No reservation found with the given ID.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void updateMadeReservationToFalse(int renterID) {
		String query = "UPDATE renter SET madeReservation = false WHERE renterID = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, renterID); // Set the renter ID

			// Execute the update
			int rowsUpdated = stmt.executeUpdate();

			// Check if the update was successful
			if (rowsUpdated > 0) {
				System.out.println("Renter's reservation status updated to false.");
			} else {
				System.out.println("No renter found with the given ID.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean storeFeedback(int reservationID, int renterID, int vehicleID, Integer driverID, int driverRating,
			int companyServiceRating, int vehicleConditionRating, int overallExperienceRating, String comments) {

		String sql = "INSERT INTO feedback (reservation_id, renter_id, vehicle_id, driver_id, "
				+ "driver_rating, company_service_rating, vehicle_condition_rating, "
				+ "overall_experience_rating, comments) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {

			// Set parameters for the query
			stmt.setInt(1, reservationID);
			stmt.setInt(2, renterID);
			stmt.setInt(3, vehicleID);
			if (driverID != null) {
				stmt.setInt(4, driverID); // Driver ID is nullable, so set it conditionally
			} else {
				stmt.setNull(4, java.sql.Types.INTEGER); // Null if no driver ID is provided
			}
			stmt.setInt(5, driverRating);
			stmt.setInt(6, companyServiceRating);
			stmt.setInt(7, vehicleConditionRating);
			stmt.setInt(8, overallExperienceRating);
			stmt.setString(9, comments);

			// Execute the insert statement
			int rowsAffected = stmt.executeUpdate();

			// If rowsAffected is greater than 0, it means the insert was successful
			return rowsAffected > 0;

		} catch (SQLException e) {
			// If an exception occurs, log the error and return false to indicate failure
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean saveToCustomerSupport(int renterID, String username, String issueType, String issueDescription) {

		String query = "INSERT INTO CustomerSupport (renterID,issueType, issueDescription,status,response,createdat) VALUES (?, ?, ?, 'pending','null',?)";
		LocalDate currentDate = LocalDate.now();

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, renterID);
			stmt.setString(2, issueType);
			stmt.setString(3, issueDescription);
			stmt.setObject(4, currentDate); // Use LocalDate for the date column

			int rowsInserted = stmt.executeUpdate();
			return rowsInserted > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int addVehicle1(Vehicle vehicle) {
		String sql = "INSERT INTO Vehicle (name, model, location,registration_number,rent_per_day,features,vehicle_type,isAvailable,rental_company_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, vehicle.getName());
			statement.setString(2, vehicle.getModel());
			statement.setString(3, vehicle.getLocation());
			statement.setString(4, vehicle.getVehicleRegistrationPlate());
			statement.setDouble(5, vehicle.getPrice());
			statement.setString(6, vehicle.getDescription());
			statement.setString(7, vehicle.getType());
			statement.setBoolean(8, vehicle.isAvailable());
			statement.setInt(9, vehicle.getCompanyID()); // Set registration plate
			// statement.setString(10, vehicle.getImage()); // Set registration plate

			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			System.err.println("Error adding vehicle to database: " + e.getMessage());
		}
		return 0; // Return 0 if the vehicle could not be added
	}

	public Vehicle getVehicleByRegistration(String registrationNumber) throws SQLException {
		String sql = "SELECT * FROM Vehicle WHERE registration_number = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, registrationNumber); // Set the registration number for the query

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				// Determine the vehicle type (either Car or Bike)
				String vehicleType = rs.getString("vehicle_type");

				// Create the correct subclass object based on vehicle type
				if ("Car".equalsIgnoreCase(vehicleType)) {
					// Create a Car object
					return new Car(rs.getInt("id"), rs.getString("name"), rs.getString("model"),
							rs.getString("location"), rs.getString("registration_number"), rs.getDouble("rent_per_day"),
							rs.getString("features"), vehicleType, rs.getBoolean("isAvailable"),
							rs.getInt("rental_company_id")
					// rs.getString("imageURL")
					);
				} else if ("Bike".equalsIgnoreCase(vehicleType)) {
					// Create a Bike object
					return new Bike(rs.getInt("id"), rs.getString("name"), rs.getString("model"),
							rs.getString("location"), rs.getString("registration_number"), rs.getDouble("rent_per_day"),
							rs.getString("features"), vehicleType, rs.getBoolean("isAvailable"),
							rs.getInt("rental_company_id")
					// rs.getString("imageURL")
					);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error retrieving vehicle by registration number: " + e.getMessage());
		}
		return null; // Return null if no vehicle found with the provided registration number
	}

	// Helper method to check if the vehicles table is empty
	public boolean isVehiclesTableEmpty() {
		String query = "SELECT COUNT(*) FROM Vehicle";
		try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1) == 0; // Returns true if the table is empty
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Return false if there is an issue or if the table is not empty
	}

	// Method to update the availability status of a vehicle
	public void updateVehicleAvailability(int vehicleId, boolean isAvailable) {
		String sql = "UPDATE Vehicle SET isAvailable = ? WHERE id = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setBoolean(1, isAvailable);
			statement.setInt(2, vehicleId);
			int rowsAffected = statement.executeUpdate();
			System.out.println("Rows affected: " + rowsAffected);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Vehicle> getAllVehicles1() {
		List<Vehicle> vehicles = new ArrayList<>();
		String sql = "SELECT * FROM Vehicle";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				int vehicleId = resultSet.getInt("id");
				String type = resultSet.getString("vehicle_type");
				boolean isAvailable = resultSet.getBoolean("isAvailable");

				Vehicle vehicle;
				if ("Car".equals(type)) {
					vehicle = new Car(vehicleId, resultSet.getString("name"), resultSet.getString("model"),
							resultSet.getString("location"), resultSet.getString("registration_number"),
							resultSet.getDouble("rent_per_day"), resultSet.getString("features"),
							resultSet.getString("vehicle_type"), isAvailable, resultSet.getInt("rental_company_id")
					// resultSet.getString("imageURL")// Get registration plate
					);
				} else {
					vehicle = new Bike(vehicleId, resultSet.getString("name"), resultSet.getString("model"),
							resultSet.getString("location"), resultSet.getString("registration_number"),
							resultSet.getDouble("rent_per_day"), resultSet.getString("features"),
							resultSet.getString("vehicle_type"), isAvailable, resultSet.getInt("rental_company_id")
					// resultSet.getString("imageURL")// // Get registration plate
					);
				}
				vehicles.add(vehicle);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vehicles;
	}

	@Override
	public boolean isVehicleAvailable(int vehicleId) {
		String query = "SELECT isAvailable FROM vehicle WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, vehicleId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("isAvailable");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Default to not available if there's an issue
	}

	/*
	 * @Override public void addReservation(int renterID, int vehicleID, int
	 * companyID, int driverID, Date startDate, Date endDate, boolean driverNeed,
	 * boolean paymentStatus, double additionalCharges) { String sql =
	 * "INSERT INTO Reservation (renterID, vehicleID, companyID, driverID, startDate, endDate, driverNeed, paymentStatus,additionalCharges) VALUES (?, ?, ?, ?, ?, ?,?,?,?)"
	 * ;
	 * 
	 * try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	 * pstmt.setInt(1, renterID); pstmt.setInt(2, vehicleID); pstmt.setInt(3,
	 * companyID); pstmt.setInt(4, driverID); pstmt.setDate(5, startDate);
	 * pstmt.setDate(6, endDate); pstmt.setBoolean(7, driverNeed);
	 * pstmt.setBoolean(8, paymentStatus); pstmt.setDouble(9, additionalCharges);
	 * 
	 * pstmt.executeUpdate(); System.out.println("Reservation added successfully.");
	 * } catch (SQLException e) { System.out.println("Error adding reservation: " +
	 * e.getMessage()); } }
	 */
	@Override
	public void addReservation(int renterID, int vehicleID, int companyID, int driverID, Date startDate, Date endDate,
			boolean isDriver, boolean paymentStatus, double additionalCharges) {
		String sql = "INSERT INTO Reservation (renterID, vehicleID, companyID, driverID, startDate, endDate, paymentStatus,additionalCharges, isDriver) VALUES (?, ?, ?, ?, ?, ?,?,?,?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, renterID);
			pstmt.setInt(2, vehicleID);
			pstmt.setInt(3, companyID);
			pstmt.setInt(4, driverID);
			pstmt.setDate(5, startDate);
			pstmt.setDate(6, endDate);
			pstmt.setBoolean(7, paymentStatus);
			pstmt.setDouble(8, additionalCharges);
			pstmt.setBoolean(9, isDriver);

			pstmt.executeUpdate();
			System.out.println("Reservation added successfully.");
		} catch (SQLException e) {
			System.out.println("Error adding reservation: " + e.getMessage());
		}
	}

	/*
	 * @Override public List<Reservation> getAllReservations() { List<Reservation>
	 * reservations = new ArrayList<>(); String sql = "SELECT * FROM Reservation";
	 * 
	 * try (Statement stmt = connection.createStatement(); ResultSet rs =
	 * stmt.executeQuery(sql)) { while (rs.next()) { Reservation reservation = new
	 * Reservation(rs.getInt("reservationID"), rs.getInt("renterID"),
	 * rs.getInt("vehicleID"), rs.getInt("companyID"), rs.getInt("driverID"),
	 * rs.getDate("startDate").toLocalDate(), rs.getDate("endDate").toLocalDate(),
	 * rs.getBoolean("driverNeed"), rs.getBoolean("paymentStatus"),
	 * rs.getDouble("additionalCharges")
	 * 
	 * ); reservations.add(reservation); } } catch (SQLException e) {
	 * System.out.println("Error retrieving reservations: " + e.getMessage()); }
	 * return reservations; }
	 */

	@Override
	public List<Reservation> getAllReservations() {
		List<Reservation> reservations = new ArrayList<>();
		String sql = "SELECT * FROM Reservation";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Reservation reservation = new Reservation(rs.getInt("reservationID"), rs.getInt("renterID"),
						rs.getInt("vehicleID"), rs.getInt("companyID"), rs.getInt("driverID"),
						rs.getDate("startDate").toLocalDate(), rs.getDate("endDate").toLocalDate(),
						rs.getBoolean("paymentStatus"), rs.getDouble("additionalCharges"), rs.getBoolean("isDriver")

				);
				reservations.add(reservation);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving reservations: " + e.getMessage());
		}
		return reservations;
	}

	@Override
	public void deleteReservation(int reservationID) {
		String sql = "DELETE FROM Reservation WHERE reservationID = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, reservationID);
			pstmt.executeUpdate();
			System.out.println("Reservation deleted successfully.");
		} catch (SQLException e) {
			System.out.println("Error deleting reservation: " + e.getMessage());
		}
	}

	@Override
	public boolean isVehicleAvailable(int vehicleID, Date startDate, Date endDate) {
		String query = "SELECT isAvailable FROM Vehicle WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, vehicleID);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				boolean isAvailable = resultSet.getBoolean("isAvailable");

				// If vehicle is not available, return false
				if (!isAvailable) {
					return false;
				}

				// Check if there are any existing reservations for this vehicle within the
				// requested date range
				String reservationQuery = "SELECT * FROM reservation WHERE vehicleID = ? AND (startDate BETWEEN ? AND ? OR endDate BETWEEN ? AND ?)";
				try (PreparedStatement reservationStatement = connection.prepareStatement(reservationQuery)) {
					reservationStatement.setInt(1, vehicleID);
					reservationStatement.setDate(2, startDate);
					reservationStatement.setDate(3, endDate);
					reservationStatement.setDate(4, startDate);
					reservationStatement.setDate(5, endDate);

					ResultSet reservationResultSet = reservationStatement.executeQuery();
					if (reservationResultSet.next()) {
						return false; // There's an overlap in the reservation dates, so the vehicle is not available
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// If the vehicle is available and no conflicting reservations, return true
		return true;
	}

	@Override
	public boolean isVehicleAvailableForDates(int vehicleID, Date startDate, Date endDate) {
		String sql = """
				SELECT COUNT(*) FROM Reservation
				WHERE vehicleID = ?
				AND ((startDate <= ? AND endDate >= ?)
				OR (startDate <= ? AND endDate >= ?))
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, vehicleID);
			pstmt.setDate(2, endDate);
			pstmt.setDate(3, startDate);
			pstmt.setDate(4, startDate);
			pstmt.setDate(5, endDate);
			ResultSet resultSet = pstmt.executeQuery();

			resultSet.next();
			return resultSet.getInt(1) == 0;
		} catch (SQLException e) {
			System.out.println("Error checking date conflicts: " + e.getMessage());
			return false;
		}
	}

	@Override
	public int saveReservationm(Reservation reservation) {

		String sql = """

				INSERT INTO Reservation (renterID, vehicleID, companyID, driverID, startDate, endDate, paymentStatus, additionalCharges, isDriver)

				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING reservationID

				""";

		// LocalDate SDate=reservation.getStartDate()

		// No need for RETURN_GENERATED_KEYS with RETURNING clause in PostgreSQL

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setInt(1, reservation.getRenterID());

			stmt.setInt(2, reservation.getVehicleID());

			stmt.setInt(3, reservation.getCompanyID());

			if (reservation.getDriverID() == -1) {

				stmt.setNull(4, java.sql.Types.INTEGER);

			} else {

				stmt.setInt(4, reservation.getDriverID());

			}

			stmt.setDate(5, Date.valueOf(reservation.getStartDate()));

			stmt.setDate(6, Date.valueOf(reservation.getEndDate()));

			stmt.setBoolean(7, reservation.isPaymentStatus());

			stmt.setDouble(8, reservation.getAdditionalPrice());

			stmt.setBoolean(9, reservation.isDriver());

			// Execute the insert and retrieve the generated ID

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

				int reservationID = rs.getInt(1); // Get the generated reservation ID

				System.out.println("Reservation created successfully with ID: " + reservationID);

				// Optionally, you can return the reservationID for further use

				return reservationID;

			} else {

				System.out.println("Failed to create reservation.");

			}

		} catch (SQLException e) {

			System.err.println("Error adding vehicle to database: " + e.getMessage());

		}

		return 0; // Return the generated ID
	}

	/*
	 * @Override public boolean saveReservation1(Reservation reservation) { try {
	 * String query =
	 * "INSERT INTO Reservation (renterID, vehicleID, companyID, driverID, startDate, endDate, driverNeed, paymentStatus,additionalCharges) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
	 * ; PreparedStatement stmt = connection.prepareStatement(query); stmt.setInt(1,
	 * reservation.getRenterID()); stmt.setInt(2, reservation.getVehicleID());
	 * stmt.setInt(3, reservation.getCompanyID()); stmt.setInt(4,
	 * reservation.getDriverID()); stmt.setDate(5,
	 * Date.valueOf(reservation.getStartDate())); stmt.setDate(6,
	 * Date.valueOf(reservation.getStartDate())); stmt.setBoolean(7,
	 * reservation.isDriver()); stmt.setBoolean(8, reservation.isPaymentStatus());
	 * stmt.setDouble(9, reservation.getAdditionalPrice());
	 * 
	 * int rowsAffected = stmt.executeUpdate(); return rowsAffected > 0; } catch
	 * (SQLException e) { e.printStackTrace(); return false; } }
	 */
	@Override
	public boolean saveReservation1(Reservation reservation) {
		try {
			String query = "INSERT INTO Reservation (renterID, vehicleID, companyID, driverID, startDate, endDate, paymentStatus,additionalCharges, isDriver) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, reservation.getRenterID());
			stmt.setInt(2, reservation.getVehicleID());
			stmt.setInt(3, reservation.getCompanyID());
			stmt.setInt(4, reservation.getDriverID());
			stmt.setDate(5, Date.valueOf(reservation.getStartDate()));
			stmt.setDate(6, Date.valueOf(reservation.getStartDate()));
			stmt.setBoolean(7, reservation.isPaymentStatus());
			stmt.setDouble(8, reservation.getAdditionalPrice());
			stmt.setBoolean(9, reservation.isDriver());

			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateDriverNeededStatus(int reservationID, boolean driverNeeded) {
		String query = "UPDATE Reservation SET isDriver = ? WHERE reservationID = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			// Set parameters for the query
			stmt.setBoolean(1, driverNeeded); // Set the driverNeeded status
			stmt.setInt(2, reservationID); // Set the reservationID

			// Execute the update query
			int rowsUpdated = stmt.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Driver needed status updated successfully for reservation ID: " + reservationID);
				return true; // Successfully updated the reservation
			} else {
				System.out.println("No matching reservation found to update for reservation ID: " + reservationID);
				return false; // No matching reservation was found
			}
		} catch (SQLException e) {
			System.err.println("Error updating driver needed status: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int storePaymentDetails(int reservationId, String paymentMethod, double amount, String cardNumber,
			String cardExpiry, String cardCvv, String accountNumber, String bankName, String easypaisaPhoneNumber) {
		// Ensure reservation_id is valid
		if (reservationId <= 0) {
			System.out.println("Error: Invalid reservation ID. Reservation not found.");
			return -1; // Exit or handle as needed
		}
		String sql = """
				INSERT INTO Payments (reservation_id, payment_method, amount, payment_status, card_number, card_expiry, card_cvv,
				           bank_account_number, bank_name, easypaisa_phone_number, payment_date)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING payment_id
				""";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Set the parameters for reservation_id, payment_method, and amount
			statement.setInt(1, reservationId); // reservation_id
			statement.setString(2, paymentMethod); // payment_method
			statement.setDouble(3, amount); // amount

			// Set payment status (assuming successful payment)
			statement.setString(4, "true"); // payment_status

			// Handle fields for different payment methods
			if ("Credit Card".equals(paymentMethod)) {
				// Credit Card specific fields
				statement.setString(5, cardNumber); // card_number for Credit Card
				statement.setString(6, cardExpiry); // card_expiry for Credit Card
				statement.setString(7, cardCvv); // card_cvv for Credit Card
				statement.setNull(8, java.sql.Types.VARCHAR); // bank_account_number (null for Credit Card)
				statement.setNull(9, java.sql.Types.VARCHAR); // bank_name (null for Credit Card)
				statement.setNull(10, java.sql.Types.VARCHAR); // easypaisa_phone_number (null for Credit Card)
			} else if ("Bank Transfer".equals(paymentMethod)) {
				// Bank Transfer specific fields
				statement.setNull(5, java.sql.Types.VARCHAR); // card_number (null for Bank Transfer)
				statement.setNull(6, java.sql.Types.VARCHAR); // card_expiry (null for Bank Transfer)
				statement.setNull(7, java.sql.Types.VARCHAR); // card_cvv (null for Bank Transfer)
				statement.setString(8, accountNumber); // bank_account_number for Bank Transfer
				statement.setString(9, bankName); // bank_name for Bank Transfer
				statement.setNull(10, java.sql.Types.VARCHAR); // easypaisa_phone_number (null for Bank Transfer)
			} else if ("Easypaisa".equals(paymentMethod)) {
				// Easypaisa specific fields
				statement.setNull(5, java.sql.Types.VARCHAR); // card_number (null for Easypaisa)
				statement.setNull(6, java.sql.Types.VARCHAR); // card_expiry (null for Easypaisa)
				statement.setNull(7, java.sql.Types.VARCHAR); // card_cvv (null for Easypaisa)
				statement.setNull(8, java.sql.Types.VARCHAR); // bank_account_number (null for Easypaisa)
				statement.setNull(9, java.sql.Types.VARCHAR); // bank_name (null for Easypaisa)
				statement.setString(10, easypaisaPhoneNumber); // easypaisa_phone_number for Easypaisa
			}

			// Set the current timestamp for payment_date (position 11)
			statement.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis())); // payment_date

			// Execute the query and retrieve the generated paymentId
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				int paymentId = rs.getInt(1); // Get the generated paymentId
				System.out.println("Payment made successfully with ID: " + paymentId);
				return paymentId;
			} else {
				System.out.println("Failed to create payment.");
			}
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
			// Optionally log the error using a logger
		}

		return -1; // Indicating failure
	}

	@Override
	public int createPayment(int reservationId, String paymentMethod, double amount, String cardNumber,
			String cardExpiry, String cardCvv, String accountNumber, String bankName, String easypaisaPhoneNumber) {

		// SQL query to insert the payment into the Payments table
		String insertSql = """
				INSERT INTO Payments (reservationId, paymentMethod, amount, cardNumber, cardExpiry, cardCvv,
				                     accountNumber, bankName, easypaisaPhoneNumber, paymentStatus)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING paymentId
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
			pstmt.setInt(1, reservationId);
			pstmt.setString(2, paymentMethod);
			pstmt.setDouble(3, amount);
			pstmt.setString(4, cardNumber);
			pstmt.setString(5, cardExpiry);
			pstmt.setString(6, cardCvv);
			pstmt.setString(7, accountNumber);
			pstmt.setString(8, bankName);
			pstmt.setString(9, easypaisaPhoneNumber);
			pstmt.setBoolean(10, true); // Assuming payment is successful (you can adjust this logic based on actual
										// payment success)

			// Execute the insert and retrieve the generated paymentId
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int paymentId = rs.getInt(1); // Get the generated payment ID
				System.out.println("Payment made successfully with ID: " + paymentId);

				// Optionally, you can return the paymentId for further use
				return paymentId;
			} else {
				System.out.println("Failed to create payment.");
			}
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
		}

		return -1;
	}

	@Override
	public double getVehicleDailyAmount(int vehicleID) {
		String query = "SELECT rent_per_day FROM Vehicle WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, vehicleID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getDouble("rent_per_day");
			} else {
				throw new SQLException("Vehicle not found for ID: " + vehicleID);
			}
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
		}
		return vehicleID;
	}

	@Override
	public void updatePaymentStatus(int reservationId, boolean paymentStatus) {
		String query = "UPDATE Reservation SET paymentstatus = ? WHERE reservationID = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setBoolean(1, paymentStatus);
			stmt.setInt(2, reservationId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
		}
	}

	@Override
	public void addImage(int vehicleId, String imageUrl) {
		String sql = "INSERT INTO Image (url, vehicle_id) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, imageUrl);
			stmt.setInt(2, vehicleId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
		}
	}

	@Override
	public String getImageByVehicleId(int vehicleId) {
		String query = "SELECT url FROM Image WHERE vehicle_id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, vehicleId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("url");
			}
		} catch (SQLException e) {
			System.out.println("Error inserting payment: " + e.getMessage());
		}
		return null;
	}

	@Override
	public String getVehicleNumberPlateByID(int vehicleID) {
		String vehiclePlate = null;
		String query = "SELECT registration_number FROM Vehicle WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setInt(1, vehicleID);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					vehiclePlate = rs.getString("registration_number");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// throw e;
		}

		return vehiclePlate;
	}

	public static List<Reservation> getCompanyReservations(int companyID) {
		List<Reservation> reservations = new ArrayList<>();

		// Logging the companyID to verify its value before query execution
		System.out.println("Fetching reservations for Company ID: " + companyID);

		String query = "SELECT * FROM Reservation WHERE companyid = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			// Set the company ID parameter
			preparedStatement.setInt(1, companyID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					reservations.add(new Reservation(resultSet.getInt("reservationid"), resultSet.getInt("renterid"),
							resultSet.getInt("vehicleid"), resultSet.getInt("driverid"), resultSet.getInt("companyid"),
							resultSet.getDate("startdate") != null ? resultSet.getDate("startdate").toLocalDate()
									: null,
							resultSet.getDate("enddate") != null ? resultSet.getDate("enddate").toLocalDate() : null,
							resultSet.getBoolean("paymentstatus"), resultSet.getDouble("additionalcharges"),
							resultSet.getBoolean("isDriver")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading reservations for company ID " + companyID + ": " + e.getMessage());
			e.printStackTrace();
		}

		return reservations;
	}

	public static List<Driver> getCompanyDrivers(int companyID) {
		List<Driver> drivers = new ArrayList<>();

		// Logging the companyID to verify its value before query execution
		System.out.println("Fetching drivers for Company ID: " + companyID);

		String query = "SELECT * FROM Driver WHERE companyID = ? AND status = 'Accepted'";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, companyID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					drivers.add(new Driver(resultSet.getInt("driverID"), resultSet.getString("driverName"),
							resultSet.getString("email"), resultSet.getString("contactNumber"),
							resultSet.getString("address"), resultSet.getString("username"),
							resultSet.getString("password"), resultSet.getString("licenseNum"),
							resultSet.getBoolean("availabilityStatus"), resultSet.getInt("companyID"),
							resultSet.getString("status")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading drivers for company ID " + companyID + ": " + e.getMessage());
			e.printStackTrace();
		}

		return drivers;
	}

	public static List<Driver> getPendingDrivers(int companyID) {
		List<Driver> drivers = new ArrayList<>();

		String query = "SELECT * FROM Driver WHERE status = 'Pending' AND companyID = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, companyID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					drivers.add(new Driver(resultSet.getInt("driverID"), resultSet.getString("driverName"),
							resultSet.getString("email"), resultSet.getString("contactNumber"),
							resultSet.getString("address"), resultSet.getString("username"),
							resultSet.getString("password"), resultSet.getString("licenseNum"),
							resultSet.getBoolean("availabilityStatus"), resultSet.getInt("companyID"),
							resultSet.getString("status")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading drivers" + ": " + e.getMessage());
			e.printStackTrace();
		}

		return drivers;
	}

	public static List<Driver> getDrivers() {
		List<Driver> drivers = new ArrayList<>();

		String query = "SELECT * FROM Driver";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					drivers.add(new Driver(resultSet.getInt("driverID"), resultSet.getString("driverName"),
							resultSet.getString("email"), resultSet.getString("contactNumber"),
							resultSet.getString("address"), resultSet.getString("username"),
							resultSet.getString("password"), resultSet.getString("licenseNum"),
							resultSet.getBoolean("availabilityStatus"), resultSet.getInt("companyID"),
							resultSet.getString("status")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading drivers" + ": " + e.getMessage());
			e.printStackTrace();
		}

		return drivers;
	}

	public static boolean deleteDriver(int driverID) {
		String query = "DELETE FROM Driver WHERE driverID = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, driverID); // Set the driver ID to find the driver to delete

			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0; // Returns true if the driver was deleted, false otherwise

		} catch (SQLException e) {
			System.err.println("Error deleting driver: " + e.getMessage());
			e.printStackTrace();
			return false; // Return false if an exception occurs
		}
	}

	public static boolean updateDriverStatus(int driverID, String newStatus) {
		String query = "UPDATE Driver SET status = ? WHERE driverID = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, newStatus); // Set new status (e.g., "Accepted")
			preparedStatement.setInt(2, driverID); // Set the driver ID to find the correct driver

			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0; // Returns true if the update was successful, false otherwise

		} catch (SQLException e) {
			System.err.println("Error updating driver status: " + e.getMessage());
			e.printStackTrace();
			return false; // Return false if an exception occurs
		}
	}

	public static boolean updateDriver(Driver driver) {
		String query = "UPDATE driver SET drivername = ?, email = ?, contactnumber = ?, address = ?, username = ?, password = ?, licensenum = ? WHERE driverid = ?";
		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, driver.getName());
			stmt.setString(2, driver.getEmail());
			stmt.setString(3, driver.getPhone());
			stmt.setString(4, driver.getAddress());
			stmt.setString(5, driver.getUsername());
			stmt.setString(6, driver.getPassword());
			stmt.setString(7, driver.getLicenseNum());
			stmt.setInt(8, driver.getUserID());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean updateDriverAvailability(int driverID, boolean newStatus) {
		String query = "UPDATE Driver SET availabilityStatus = ? WHERE driverID = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setBoolean(1, newStatus); // Set new status (e.g., "Accepted")
			preparedStatement.setInt(2, driverID); // Set the driver ID to find the correct driver

			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0; // Returns true if the update was successful, false otherwise

		} catch (SQLException e) {
			System.err.println("Error updating driver availability: " + e.getMessage());
			e.printStackTrace();
			return false; // Return false if an exception occurs
		}
	}

	public static boolean assignDriverToReservation(int reservationID, int driverID) {
		String query = "UPDATE reservation SET driverid = ? WHERE reservationid = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, driverID);
			statement.setInt(2, reservationID);
			int rowsUpdated = statement.executeUpdate();
			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getVehicleNum(int vehicleId) {
		String query = "SELECT registration_number FROM Vehicle WHERE id = ?";
		String vehicleDetails = null;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, vehicleId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					vehicleDetails = rs.getString("registration_number");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vehicleDetails;
	}

	public static int getVehicleID(String plateNumber) {
		String query = "SELECT id FROM Vehicle WHERE registration_number = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, plateNumber);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if vehicle not found
	}

	public static int getRenterID(String rusername) {
		String query = "SELECT renterID FROM Renter WHERE username = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, rusername);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("renterID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if renter not found
	}

	public static int getDriver_ID(String dusername) {
		String query = "SELECT driverID FROM Driver WHERE username = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, dusername);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("driverID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if driver not found
	}

	public static int getRentalID(String rusername) {
		String query = "SELECT companyID FROM RentalCompany WHERE username = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, rusername);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("companyID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if driver not found
	}

	public static String getRenterUsername(int renterId) {
		String query = "SELECT username FROM Renter WHERE renterid = ?";
		String renterName = null;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, renterId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					renterName = rs.getString("username");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return renterName;
	}

	public static boolean updateApplicationStatus(int driverId, String status) {
		String query = "UPDATE Driver SET status = ? WHERE id = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, status);
			stmt.setInt(2, driverId);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getDriverUsername(int driverId) {
		String query = "SELECT username FROM Driver WHERE driverid = ?";
		String driverName = null;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			stmt.setInt(1, driverId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					driverName = rs.getString("username");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return driverName;
	}

	@Override
	public int getUserID(String username, String password, String role) {
		String query = null;

		// Determine the query based on the role
		if (role.equals("Renter")) {
			query = "SELECT renterid FROM renter WHERE username = ? AND password = ?";
		} else if (role.equals("RentalCompany")) {
			query = "SELECT user_id FROM RentalCompany WHERE username = ? AND password = ?";
		} else if (role.equals("Driver")) {
			query = "SELECT user_id FROM Driver WHERE username = ? AND password = ?";
		} else if (role.equals("Admin")) {
			query = "SELECT user_id FROM AppAdmin WHERE username = ? AND password = ?";
		} else {
			System.out.println("Invalid role specified.");
			return 0; // Invalid role, return 0
		}

		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, username);
			stmnt.setString(2, password);
			ResultSet rs = stmnt.executeQuery();

			// If a result is found, return the user_id
			if (rs.next()) {
				return rs.getInt("renterid");
			}
		} catch (SQLException e) {
			System.out.println("Failed to retrieve user: " + e.getMessage());
		}

		return 0; // Return 0 if no user found
	}

	public static List<Feedback> getFeedbackList(int companyID) {
		List<Feedback> feedbackList = new ArrayList<>();

		// Logging the companyID to verify its value before query execution
		System.out.println("Fetching feedback for Company ID: " + companyID);

		String query = """
				SELECT
				    f.feedback_id,
				    f.reservation_id,
				    f.renter_id,
				    f.vehicle_id,
				    f.driver_id,
				    f.driver_rating,
				    f.company_service_rating,
				    f.vehicle_condition_rating,
				    f.overall_experience_rating,
				    f.comments
				FROM
				    feedback f
				INNER JOIN
				    reservation r ON f.reservation_id = r.reservationid
				INNER JOIN
				    vehicle v ON r.vehicleid = v.id
				WHERE
				    v.rental_company_id = ?;
				""";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			// Set the company ID parameter
			stmt.setInt(1, companyID);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int feedbackID = rs.getInt("feedback_id");
					int reservationID = rs.getInt("reservation_id");
					int renterID = rs.getInt("renter_id");
					int vehicleID = rs.getInt("vehicle_id");
					int driverID = rs.getInt("driver_id");
					int driverRating = rs.getInt("driver_rating");
					int companyServiceRating = rs.getInt("company_service_rating");
					int vehicleConditionRating = rs.getInt("vehicle_condition_rating");
					int overallExperienceRating = rs.getInt("overall_experience_rating");
					String comments = rs.getString("comments");

					Feedback feedback = new Feedback(feedbackID, reservationID, renterID, vehicleID, driverID,
							driverRating, companyServiceRating, vehicleConditionRating, overallExperienceRating,
							comments);
					feedbackList.add(feedback);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return feedbackList;
	}

	/*
	 * public static List<Reservation> getReservations() { List<Reservation>
	 * reservations = new ArrayList<>();
	 * 
	 * String query = "SELECT * FROM Reservation";
	 * 
	 * try (Connection connection = DriverManager.getConnection(url, username,
	 * password); Statement statement = connection.createStatement(); ResultSet
	 * resultSet = statement.executeQuery(query)) {
	 * 
	 * while (resultSet.next()) { reservations.add(new
	 * Reservation(resultSet.getInt("reservationid"), resultSet.getInt("renterid"),
	 * resultSet.getInt("vehicleid"), resultSet.getInt("driverid"),
	 * resultSet.getInt("companyid"), resultSet.getDate("startdate") != null ?
	 * resultSet.getDate("startdate").toLocalDate() : null,
	 * resultSet.getDate("enddate") != null ?
	 * resultSet.getDate("enddate").toLocalDate() : null,
	 * resultSet.getBoolean("isdriver"), resultSet.getBoolean("paymentstatus"),
	 * resultSet.getDouble("additionalcharges"))); } } catch (SQLException e) {
	 * System.err.println("Error loading reservations: " + e.getMessage());
	 * e.printStackTrace(); } return reservations; }
	 */
	public static List<Reservation> getReservations() {
		List<Reservation> reservations = new ArrayList<>();

		String query = "SELECT * FROM Reservation";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				reservations.add(new Reservation(resultSet.getInt("reservationid"), resultSet.getInt("renterid"),
						resultSet.getInt("vehicleid"), resultSet.getInt("driverid"), resultSet.getInt("companyid"),
						resultSet.getDate("startdate") != null ? resultSet.getDate("startdate").toLocalDate() : null,
						resultSet.getDate("enddate") != null ? resultSet.getDate("enddate").toLocalDate() : null,
						resultSet.getBoolean("paymentstatus"), resultSet.getDouble("additionalcharges"),
						resultSet.getBoolean("isdriver")));
			}
		} catch (SQLException e) {
			System.err.println("Error loading reservations: " + e.getMessage());
			e.printStackTrace();
		}
		return reservations;
	}

	@Override
	public boolean addVehicle(Vehicle v) {
		if (!isConnected || connection == null) {
			return false; // Return false if there's no connection
		}

		String insertVehicleQuery = """
				    INSERT INTO Vehicle
				    (name, model, registration_number, rent_per_day, features, vehicle_type, location, isAvailable, rental_company_ID)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
				""";
		String insertImageQuery = "INSERT INTO Image (url, vehicle_id) VALUES (?, ?)";

		try (PreparedStatement vehicleStmt = connection.prepareStatement(insertVehicleQuery,
				Statement.RETURN_GENERATED_KEYS);
				PreparedStatement imageStmt = connection.prepareStatement(insertImageQuery)) {

			// Set vehicle details
			vehicleStmt.setString(1, v.getName());
			vehicleStmt.setString(2, v.getModel());
			vehicleStmt.setString(3, v.getRegistrationNum());
			vehicleStmt.setDouble(4, v.getRentPerDay());
			vehicleStmt.setString(5, v.getFeatures());
			vehicleStmt.setString(6, v.getVehicleType());
			vehicleStmt.setString(7, v.getLocation());
			vehicleStmt.setBoolean(8, v.isAvailable());
			vehicleStmt.setInt(9, v.getCompanyID());

			// Execute update and get generated vehicle ID
			int affectedRows = vehicleStmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet rs = vehicleStmt.getGeneratedKeys()) {
					if (rs.next()) {
						int vehicleId = rs.getInt(1);

						// Insert the single image URL into the Image table with the vehicle ID
						String imageUrl = v.getImageUrl(); // Get the single image URL
						if (imageUrl != null && !imageUrl.isEmpty()) {
							imageStmt.setString(1, imageUrl);
							imageStmt.setInt(2, vehicleId);
							imageStmt.executeUpdate();
						}

						System.out.println("Vehicle added successfully with an image!");
						return true; // Return true for successful operation
					}
				}
			} else {
				System.out.println("Failed to add vehicle - no rows affected.");
				return false; // Return false if no rows were affected
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Return false on exception
		}
		return false; // Default return false in case of unexpected behavior
	}

	public static List<Vehicle> getAllVehicles() {
		List<Vehicle> vehicles = new ArrayList<>();
		String query = "SELECT * FROM Vehicle";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Vehicle vehicle = mapResultSetToVehicle(resultSet);
				vehicle.setImageUrl(getImageUrl(vehicle.getId()));
				vehicles.add(vehicle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vehicles;
	}

	public static List<Vehicle> getCompanyVehicles(int companyID) {
		List<Vehicle> vehicles = new ArrayList<>();
		String query = "SELECT * FROM Vehicle WHERE rental_company_id = ?";

		System.out.println("Fetching vehicles for Company ID: " + companyID);

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, companyID);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Vehicle vehicle = mapResultSetToVehicle(resultSet);
					vehicle.setImageUrl(getImageUrl(vehicle.getId()));
					vehicles.add(vehicle);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vehicles;
	}

	private static Vehicle mapResultSetToVehicle(ResultSet resultSet) throws Exception {
		int id = resultSet.getInt("id");
		String name = resultSet.getString("name");
		String model = resultSet.getString("model");
		String regNum = resultSet.getString("registration_number");
		String vehicleType = resultSet.getString("vehicle_type");
		double rentPerDay = resultSet.getDouble("rent_per_day");
		String features = resultSet.getString("features");
		String location = resultSet.getString("location");
		boolean isAvailable = resultSet.getBoolean("isAvailable");
		int companyID = resultSet.getInt("rental_company_id");

		// Create the appropriate subclass instance based on vehicle_type
		switch (vehicleType.toLowerCase()) {
		case "car":
			return new Car(id, name, model, location, regNum, rentPerDay, features, isAvailable, companyID);
		case "bike":
			return new Car(id, name, model, location, regNum, rentPerDay, features, isAvailable, companyID);
		default:
			throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
		}
	}

	@Override
	public boolean updateVehicle(Vehicle vehicle) {
		String updateQuery = """
				    UPDATE Vehicle SET
				    name = ?,
				    model = ?,
				    location = ?,
				    registration_number = ?,
				    rent_per_day = ?,
				    features = ?,
				    vehicle_type = ?,
				    isAvailable = ?
				    WHERE id = ?
				""";

		String updateImageQuery = """
				    UPDATE Image
				    SET url = ?
				    WHERE vehicle_id = ?
				""";

		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
				PreparedStatement updateImageStmt = conn.prepareStatement(updateImageQuery)) {

			// Validate vehicle ID
			System.out.println("Vehicle ID: " + vehicle.getId());

			// Update vehicle details
			updateStmt.setString(1, vehicle.getName());
			updateStmt.setString(2, vehicle.getModel());
			updateStmt.setString(3, vehicle.getLocation());
			updateStmt.setString(4, vehicle.getRegistrationNum());
			updateStmt.setDouble(5, vehicle.getRentPerDay());
			updateStmt.setString(6, vehicle.getFeatures());
			updateStmt.setString(7, vehicle.getVehicleType());
			updateStmt.setBoolean(8, vehicle.isAvailable());
			updateStmt.setInt(9, vehicle.getId());

			int rowsUpdated = updateStmt.executeUpdate();
			System.out.println("Rows updated: " + rowsUpdated); // Debugging line

			if (rowsUpdated > 0) {
				// Update the image URL for the vehicle
				String imageUrl = vehicle.getImageUrl();
				System.out.println("Image URL: " + imageUrl); // Debugging line

				if (imageUrl != null && !imageUrl.isEmpty()) {
					updateImageStmt.setString(1, imageUrl);
					updateImageStmt.setInt(2, vehicle.getId());
					int imageRowsUpdated = updateImageStmt.executeUpdate();
					System.out.println("Image rows updated: " + imageRowsUpdated); // Debugging line
				}
				return true;
			} else {
				System.out.println("No vehicle found with ID: " + vehicle.getId()); // Debugging line
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateReservation(Reservation r) {
		String updateReservationQuery = "UPDATE reservation SET " + "renterid = ?, " + "vehicleid = ?, "
				+ "driverid = ?, " + "startdate = ?, " + "enddate = ?, " + "paymentstatus = ?, "
				+ "additionalcharges = ? " + "WHERE reservationid = ?";

		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = conn.prepareStatement(updateReservationQuery)) {

			// Set values in the query
			stmt.setInt(1, r.getRenterID());
			stmt.setInt(2, r.getVehicleID());

			// Check if driverID is -1, set to NULL if true
			if (r.getDriverID() == -1) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(3, r.getDriverID());
			}

			stmt.setDate(4, Date.valueOf(r.getStartDate()));
			stmt.setDate(5, Date.valueOf(r.getEndDate()));
			stmt.setBoolean(6, r.isPaymentStatus());
			stmt.setDouble(7, r.getAdditionalCharges());
			stmt.setInt(8, r.getReservationID());

			// Execute update query
			int rowsUpdated = stmt.executeUpdate();

			// Return true if at least one row was updated
			return rowsUpdated > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteVehicle(Vehicle vehicle) {
		if (!vehicle.isAvailable()) {
			System.out.println("Vehicle is not available for deletion.");
			return false;
		}

		String query = "DELETE FROM Vehicle WHERE registration_number = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, vehicle.getRegistrationNum());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean saveReservation(Reservation r) {
		String sql = "INSERT INTO Reservation (renterid, driverid, vehicleid, companyid, startdate, enddate, additionalcharges, paymentstatus, isDriver)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// Set parameters for the query
			stmt.setInt(1, r.getRenterID());

			// Check if driverID is -1, set to NULL if true
			if (r.getDriverID() == -1) {
				stmt.setNull(2, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(2, r.getDriverID());
			}

			stmt.setInt(3, r.getVehicleID());
			stmt.setInt(4, r.getCompanyID());
			stmt.setDate(5, java.sql.Date.valueOf(r.getStartDate())); // Convert LocalDate to java.sql.Date
			stmt.setDate(6, java.sql.Date.valueOf(r.getEndDate())); // Convert LocalDate to java.sql.Date
			stmt.setDouble(7, r.getAdditionalCharges());
			stmt.setBoolean(8, r.isPaymentStatus());
			stmt.setBoolean(9, r.isDriver());

			// Execute update and return success status
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace(); // Log the exception
			return false; // Return false if an error occurs
		}
	}

	@Override
	public boolean deleteReservation(Reservation reservation) {
		// SQL query to delete the reservation from the database
		String query = "DELETE FROM Reservation WHERE reservationid = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			// Assuming Reservation has a method `getId` to fetch its unique identifier
			stmt.setInt(1, reservation.getReservationID());

			// Execute the query and check the number of affected rows
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace(); // Log the exception for debugging
			return false; // Return false if an exception occurs
		}
	}

	public static Vehicle getVehicle(String plateNumber) {
		String query = "SELECT * FROM Vehicle WHERE registration_number = ?";
		String imageQuery = "SELECT url FROM image WHERE vehicle_id = ?";
		Vehicle vehicle = null;

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query);
				PreparedStatement imageStmt = connection.prepareStatement(imageQuery)) {

			// Fetch vehicle details
			stmt.setString(1, plateNumber);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Retrieve data
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String model = rs.getString("model");
				double rentPerDay = rs.getDouble("rent_per_day");
				String features = rs.getString("features");
				String location = rs.getString("location");
				boolean isAvailable = rs.getBoolean("isAvailable");
				String vehicleType = rs.getString("vehicle_type");

				// Instantiate the correct vehicle type
				if ("Car".equals(vehicleType)) {
					vehicle = new Car(id, name, plateNumber, model, rentPerDay, location, features, isAvailable);
				} else if ("Bike".equals(vehicleType)) {
					vehicle = new Bike(id, name, plateNumber, model, rentPerDay, location, features, isAvailable);
				}

				// Fetch and set the single image URL
				if (vehicle != null) {
					imageStmt.setInt(1, id);
					ResultSet imageRs = imageStmt.executeQuery();

					if (imageRs.next()) {
						String imageUrl = imageRs.getString("url");
						vehicle.setImageUrl(imageUrl); // Ensure Vehicle has setImageUrl() method
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return vehicle;
	}

	@Override
	public boolean saveFeedback(Feedback f) {
		String query = "INSERT INTO Feedback (reservation_id, renter_id, vehicle_id, driver_id, driver_rating, "
				+ "company_service_rating, vehicle_condition_rating, overall_experience_rating, comments) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, f.getReservationID());
			stmt.setInt(2, f.getRenterID());
			stmt.setInt(3, f.getVehicleID());
			stmt.setInt(4, f.getDriverID());
			stmt.setInt(5, f.getDriverRating());
			stmt.setInt(6, f.getCompanyServiceRating());
			stmt.setInt(7, f.getVehicleConditionRating());
			stmt.setInt(8, f.getOverallExperienceRating());
			stmt.setString(9, f.getComments());

			int rowsInserted = stmt.executeUpdate();
			return rowsInserted > 0; // Return true if feedback is saved successfully
		} catch (SQLException e) {
			System.err.println("Error saving feedback: " + e.getMessage());
			return false;
		}
	}

	// Static function to get feedbacks for a specific vehicleID
	public static List<Feedback> getFeedback(int vehicleID) {
		String query = "SELECT * FROM Feedback WHERE vehicle_id = ?";
		List<Feedback> feedbackList = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, vehicleID);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Feedback feedback = new Feedback(rs.getInt("feedback_id"), rs.getInt("reservation_id"),
							rs.getInt("renter_id"), rs.getInt("vehicle_id"), rs.getInt("driver_id"),
							rs.getInt("driver_rating"), rs.getInt("company_service_rating"),
							rs.getInt("vehicle_condition_rating"), rs.getInt("overall_experience_rating"),
							rs.getString("comments"));
					feedbackList.add(feedback);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching feedback by vehicle ID: " + e.getMessage());
		}
		return feedbackList;
	}

	public static String getImageUrl(int vehicleId) {
		String imageUrl = null;
		String query = "SELECT url FROM Image WHERE vehicle_id = ? LIMIT 1";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, vehicleId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				imageUrl = rs.getString("url");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return imageUrl;
	}

	@Override
	public boolean userExists(Object identifier, String role) {
		String query = null;

		// Determine the query based on the role and type of identifier
		if (role.equals("Renter")) {
			if (identifier instanceof String) {
				query = "SELECT 1 FROM Renter WHERE username = ?";
			} else if (identifier instanceof Integer) {
				query = "SELECT 1 FROM Renter WHERE renterID = ?";
			}
		} else if (role.equals("RentalCompany")) {
			if (identifier instanceof String) {
				query = "SELECT 1 FROM RentalCompany WHERE username = ?";
			} else if (identifier instanceof Integer) {
				query = "SELECT 1 FROM RentalCompany WHERE companyID = ?";
			}
		} else if (role.equals("Driver")) {
			if (identifier instanceof String) {
				query = "SELECT 1 FROM Driver WHERE username = ?";
			} else if (identifier instanceof Integer) {
				query = "SELECT 1 FROM Driver WHERE driverID = ?";
			}
		} else if (role.equals("Admin")) {
			if (identifier instanceof String) {
				query = "SELECT 1 FROM AppAdmin WHERE username = ?";
			} else if (identifier instanceof Integer) {
				query = "SELECT 1 FROM AppAdmin WHERE adminID = ?";
			}
		}

		// Execute the query if it was set
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			if (identifier instanceof String) {
				stmnt.setString(1, (String) identifier);
			} else if (identifier instanceof Integer) {
				stmnt.setInt(1, (Integer) identifier);
			}

			ResultSet rs = stmnt.executeQuery();
			return rs.next(); // Returns true if a result is found
		} catch (SQLException e) {
			System.out.println("Failed to check user existence: " + e.getMessage());
			return false;
		}
	}

	@Override
	public int insertUser(User user) {
		String role = user.getClass().getSimpleName();

		if (userExists(user.getUsername(), role)) {
			showAlert(Alert.AlertType.ERROR, "Duplicate User",
					role + " with this username already exists in the database.");
			return 0;
		} else {
			String query = null;
			if (role.equals("Renter")) {
				query = "INSERT INTO Renter (renterName, username, password, email, phone, address) VALUES (?, ?, ?, ?, ?, ?) RETURNING renterID";
			} else if (role.equals("RentalCompany")) {
				query = "INSERT INTO RentalCompany (companyName, username, password, email, contactNumber, address) VALUES (?, ?, ?, ?, ?, ?) RETURNING companyID";
			} else if (role.equals("Driver")) {
				int companyID = getCompanyID(((Driver) user).getCompanyUsername()); // getting company ID
				if (companyID == 0) {
					return 0; // Stop the process if the company does not exist
				}
				((Driver) user).setCompanyID(companyID);
				query = "INSERT INTO Driver (driverName, username, password, email, contactNumber, address, licenseNum,companyID) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING driverID";
			} else if (role.equals("Admin")) {
				query = "INSERT INTO AppAdmin (adminName, username, password, email, phone, address) VALUES (?, ?, ?, ?, ?, ?) RETURNING adminID";
			} else {
				System.out.println("Invalid role specified.");
				return 0;
			}

			try (PreparedStatement stmnt = connection.prepareStatement(query)) {

				stmnt.setString(1, user.getName());
				stmnt.setString(2, user.getUsername());
				stmnt.setString(3, user.getPassword());
				stmnt.setString(4, user.getEmail());
				stmnt.setString(5, user.getPhone());
				stmnt.setString(6, user.getAddress());

				// Only set the licenseNum if the role is Driver
				if (role.equals("Driver")) {
					stmnt.setString(7, ((Driver) user).getLicenseNum());
					stmnt.setInt(8, ((Driver) user).getCompanyID());
				}

				ResultSet rs = stmnt.executeQuery();
				if (rs.next()) {
					int generatedUserID = rs.getInt(1);
					System.out.println(role + " added successfully with ID: " + generatedUserID + " and username: "
							+ user.getUsername());
					return generatedUserID; // Return the generated userID if needed
				}
			} catch (SQLException e) {
				System.out.println("Failed to insert user: " + e.getMessage());
				// Insertion failed
			}
		}
		return 0;
	}

	@Override
	public void deleteUser(int userID, String role) {

		if (!userExists(userID, role)) {
			showAlert(Alert.AlertType.ERROR, "User Not Found", role + " with ID " + userID + " does not exist.");
			return;
		}

		// Prompt the admin for confirmation
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Deletion");
		confirmationAlert.setHeaderText(null);
		confirmationAlert.setContentText("Are you sure you want to delete " + role + " with ID " + userID + "?");
		Optional<ButtonType> result = confirmationAlert.showAndWait();

		// If the admin cancels the deletion, exit
		if (result.isEmpty() || result.get() != ButtonType.OK) {
			showAlert(Alert.AlertType.INFORMATION, "Action Canceled", "The deletion operation has been canceled.");
			return;
		}

		// Determining the delete query based on the role
		String deleteQuery = null;
		switch (role) {
		case "Renter":
			deleteQuery = "DELETE FROM Renter WHERE renterID = ?";
			break;
		case "RentalCompany":
			deleteQuery = "DELETE FROM RentalCompany WHERE companyID = ?";
			break;
		case "Driver":
			deleteQuery = "DELETE FROM Driver WHERE driverID = ?";
			break;
		case "Admin":
			deleteQuery = "DELETE FROM AppAdmin WHERE adminID = ?";
			break;
		default:
			showAlert(Alert.AlertType.ERROR, "Invalid Role", "The specified role is not valid.");
			return;
		}

		// Execute the delete query
		try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
			deleteStmt.setInt(1, userID);
			int rowsAffected = deleteStmt.executeUpdate();

			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						role + " with ID " + userID + " was successfully deleted.");
			} else {
				showAlert(Alert.AlertType.ERROR, "Deletion Failed",
						"Failed to delete " + role + " with ID " + userID + ".");
			}
		} catch (SQLException e) {
			System.out.println("Error deleting user: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the user.");
		}
	}

	@Override
	public void updateUser(int userID, String selectedAttribute, String updatedValue, String role) {
		if (!userExists(userID, role)) {
			showAlert(Alert.AlertType.ERROR, "User Not Found", role + " with ID " + userID + " does not exist.");
			return;
		}

		String validAttributes = switch (role) {
		case "Renter" -> "renterName, password, email, phone, address";
		case "RentalCompany" -> "companyName, password, email, contactNumber, address";
		case "Driver" -> "driverName, password, email, contactNumber, address";
		case "AppAdmin" -> "adminName, password, email, phone, address";
		default -> null;
		};

		if (validAttributes == null || !validAttributes.contains(selectedAttribute)) {
			showAlert(Alert.AlertType.ERROR, "Invalid Attribute",
					"The selected attribute is not valid for the role: " + role);
			return;
		}

		// Build the SQL query
		String tableName = switch (role) {
		case "Renter" -> "Renter";
		case "RentalCompany" -> "RentalCompany";
		case "Driver" -> "Driver";
		case "AppAdmin" -> "AppAdmin";
		default -> null;
		};

		if (tableName == null) {
			showAlert(Alert.AlertType.ERROR, "Invalid Role", "The specified role is invalid.");
			return;
		}

		String idColumn = switch (role) {
		case "Renter" -> "renterID";
		case "RentalCompany" -> "companyID";
		case "Driver" -> "driverID";
		case "AppAdmin" -> "adminID";
		default -> null;
		};

		String query = "UPDATE " + tableName + " SET " + selectedAttribute + " = ? WHERE " + idColumn + " = ?";

		// Execute the query
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, updatedValue);
			stmnt.setInt(2, userID);

			int rowsAffected = stmnt.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						selectedAttribute + " was successfully updated for " + role + " with ID: " + userID);
			} else {
				showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update " + role + " with ID: " + userID);
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while updating the user: " + e.getMessage());
		}

	}

	@Override
	public void displayUser(String username, String role) {
		String query = null;
		if (role.equals("Renter")) {
			query = "SELECT * FROM Renter WHERE username = ?";
		} else if (role.equals("RentalCompany")) {
			query = "SELECT * FROM RentalCompany WHERE username = ?";
		} else if (role.equals("Driver")) {
			query = "SELECT * FROM Driver WHERE username = ?";
		} else if (role.equals("Admin")) {
			query = "SELECT * FROM AppAdmin WHERE username = ?";
		}

	}

	@Override
	public int getCompanyID(String companyUsername) {
		String query = "SELECT companyID FROM RentalCompany WHERE username = ?";
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, companyUsername);
			ResultSet rs = stmnt.executeQuery();
			if (rs.next()) {
				return rs.getInt("companyID"); // Return the companyID if found
			} else {
				showAlert(Alert.AlertType.ERROR, "Company Not Found",
						"No RentalCompany found with username: " + companyUsername);
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch companyID: " + e.getMessage());
		}
		return 0; // Return 0 if the company is not found or an error occurs
	}

	@Override
	public ResultSet displayAllUsers(String role) {
		String query = null;

		if (role.equals("Renter")) {
			query = "SELECT * FROM Renter";
		} else if (role.equals("RentalCompany")) {
			query = "SELECT * FROM RentalCompany";
		} else {
			System.out.println("Invalid role specified.");
			return null;
		}

		try {
			Statement stmt = connection.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Error fetching users: " + e.getMessage());
			return null;
		}
	}

	@Override
	public boolean checkLogin(String username, String password, String role) {
		String query = null;

		// Determine the query based on the role
		if (role.equals("Renter")) {
			query = "SELECT 1 FROM Renter WHERE username = ? AND password = ?";
		} else if (role.equals("RentalCompany")) {
			query = "SELECT 1 FROM RentalCompany WHERE username = ? AND password = ?";
		} else if (role.equals("Driver")) {
			query = "SELECT 1 FROM Driver WHERE username = ? AND password = ?";
		} else if (role.equals("Admin")) {
			query = "SELECT 1 FROM AppAdmin WHERE username = ? AND password = ?";
		} else {
			System.out.println("Invalid role specified.");
			return false;
		}

		// Execute the query if it was set
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, username);
			stmnt.setString(2, password);
			ResultSet rs = stmnt.executeQuery();
			return rs.next(); // Returns true if a result is found
		} catch (SQLException e) {
			System.out.println("Failed to retrieve user: " + e.getMessage());
			return false;
		}
	}

	@Override
	public void updateCustomerSupport(int supportID, String response, String status) {
		String query = "UPDATE CustomerSupport SET response = ?,status = ? WHERE supportID = ?";

		// Execute the query
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, response);
			stmnt.setString(2, status);
			stmnt.setInt(3, supportID);

			int rowsAffected = stmnt.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						"Customer Support was successfully updated for ID " + supportID);
			} else {
				showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update ");
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while updating the user: " + e.getMessage());
		}

	}

	@Override
	public void deleteSupport(int ID) {
		String checkQuery = "SELECT 1 FROM CustomerSupport WHERE supportID = ?";
		String deleteQuery = "DELETE FROM CustomerSupport WHERE supportID = ?";

		try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
			checkStmt.setInt(1, ID);
			ResultSet rs = checkStmt.executeQuery();

			if (!rs.next()) {
				showAlert(Alert.AlertType.ERROR, "Entry Not Found",
						"Customer Support entry with ID " + ID + " does not exist.");
				return;
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while checking for the entry: " + e.getMessage());
			return;
		}

		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Deletion");
		confirmationAlert.setHeaderText(null);
		confirmationAlert.setContentText("Are you sure you want to delete Customer Support with ID " + ID + "?");
		Optional<ButtonType> result = confirmationAlert.showAndWait();

		if (result.isEmpty() || result.get() != ButtonType.OK) {
			showAlert(Alert.AlertType.INFORMATION, "Action Canceled", "The deletion operation has been canceled.");
			return;
		}

		try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
			deleteStmt.setInt(1, ID);
			int rowsAffected = deleteStmt.executeUpdate();

			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						"Customer Support entry with ID " + ID + " was successfully deleted.");
			} else {
				showAlert(Alert.AlertType.ERROR, "Deletion Failed",
						"Failed to delete Customer Support entry with ID " + ID + ".");
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while deleting the entry: " + e.getMessage());
		}
	}

	@Override
	public ResultSet displayAllCustomerSupport() {
		String query = "SELECT * FROM CustomerSupport";

		try {
			PreparedStatement stmnt = connection.prepareStatement(query);
			return stmnt.executeQuery(); // Return the ResultSet to process it later
		} catch (SQLException e) {
			System.out.println("Error fetching customer support data: " + e.getMessage());
			return null;
		}
	}

	@Override
	public ResultSet displayReservationsForDriver(int ID) {
		String query = "SELECT * FROM Reservation WHERE driverID = ?";

		try {
			// Prepare the statement
			PreparedStatement stmnt = connection.prepareStatement(query);
			stmnt.setInt(1, ID);
			return stmnt.executeQuery();

		} catch (SQLException e) {
			// Log the exception and return null
			System.out.println("Error fetching reservations for driver with ID " + ID + ": " + e.getMessage());
			return null;
		}
	}

	public static int getDriverID(String dusername) {
		String query = "SELECT driverID FROM Driver WHERE username = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, dusername);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("driverID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if driver not found
	}

	public boolean licenseExists(String licenseNum) {
		String query = "SELECT 1 FROM Driver WHERE licenseNum = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, licenseNum);
			ResultSet rs = pstmt.executeQuery();
			return rs.next(); // Returns true if a result is found, indicating licenseNum already exists
		} catch (SQLException e) {
			System.out.println("Failed to check license number: " + e.getMessage());
			return false;
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null); // You can add a header if needed
		alert.setContentText(message);
		alert.showAndWait();
	}

	@Override
	public void searchUser(int attribute, String value) {
	}

	@Override
	public int getDriverIDFromDB(String username) {
		String query = "SELECT driverID FROM Driver WHERE username = ?";
		try (PreparedStatement stmnt = connection.prepareStatement(query)) {
			stmnt.setString(1, username);
			ResultSet rs = stmnt.executeQuery();
			if (rs.next()) {
				return rs.getInt("driverID"); // Return the driverID if found
			} else {
				// Show an alert if no driver is found with the given username
				showAlert(Alert.AlertType.ERROR, "Driver Not Found", "No Driver found with username: " + username);
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch driverID: " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while fetching the driver ID: " + e.getMessage());
		}
		return 0; // Return -1 if the driver is not found or an error occurs
	}

	@Override
	public Driver getDriver(int ID) {
		String query = "SELECT * FROM Driver WHERE driverID = ?"; // SQL query to fetch driver details by ID
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, ID); // Set the driverID in the query
			ResultSet rs = stmt.executeQuery(); // Execute the query

			if (rs.next()) {
				// Create a Driver object and populate its fields with the database result
				Driver driver = new Driver();
				driver.setUserID(rs.getInt("driverID"));
				driver.setName(rs.getString("driverName"));
				driver.setEmail(rs.getString("email"));
				driver.setPhone(rs.getString("contactNumber"));
				driver.setAddress(rs.getString("address"));
				driver.setUsername(rs.getString("username"));
				driver.setPassword(rs.getString("password"));
				driver.setLicenseNum(rs.getString("licenseNum"));
				driver.setAvailable(rs.getBoolean("availabilityStatus"));

				return driver; // Return the populated Driver object
			} else {
				// Show an alert if no driver is found
				showAlert(Alert.AlertType.ERROR, "Driver Not Found", "No driver found with ID: " + ID);
			}
		} catch (SQLException e) {
			// Show an alert if there is a database error
			showAlert(Alert.AlertType.ERROR, "Database Error",
					"An error occurred while fetching driver details: " + e.getMessage());
		}
		return null; // Return null if no driver is found or an error occurs
	}
}
