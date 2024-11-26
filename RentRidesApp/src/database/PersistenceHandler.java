package database;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.List;

import model.reservation.Feedback;
import model.reservation.Reservation;
import model.user.*;
import model.vehicle.Vehicle;

public interface PersistenceHandler {
	boolean userExists(Object identifier,String role);
	
	boolean checkLogin(String username,String password,String role);
	
	int insertUser(User user);
	
	void deleteUser(int userID,String role);
	
	void updateUser(int userID, String selectedAttribute, String uptadedValue, String role);
	
	void displayUser(String username,String role);
	
	void searchUser(int attribute,String value);
	
	ResultSet displayAllUsers(String role);
	
	int getDriverIDFromDB(String username);
	
	int getCompanyID(String companyUsername);
	
	Driver getDriver(int ID);
	
	ResultSet displayAllCustomerSupport();
	
	void updateCustomerSupport(int supportID,String response,String status);
	
	void deleteSupport(int ID);
	
	ResultSet displayReservationsForDriver(int driverID);
	
	boolean addVehicle(Vehicle v);
	
	boolean updateVehicle(Vehicle vehicle);
	
	boolean updateReservation(Reservation r);
	
	boolean deleteVehicle(Vehicle vehicle);
	
	boolean saveReservation1(Reservation r);
	
	boolean deleteReservation(Reservation reservation);
	
	boolean saveFeedback(Feedback f);
	
	void updateMadeReservation(int renterID, boolean madeReservation);

	boolean checkMadeReservation(int renterID);
	Reservation getActiveReservationByRenterID(int renterID) ;
	double getPaymentAmountByReservationID(int reservationID);
	void updateReservationInDatabase(int reservationID, int renterID, double additional_price);
	int getVehicleIDByRegistrationPlate(String registrationPlate);
	String getVehicleNameByID(int vehicleID);
	String getRenterNameByID(int renterID);
	void setVehicleAvailable(int vehicleID);
	int getRenterIDFromReservation(int reservationID);
	void updateMadeReservationToFalse(int renterID);
	boolean storeFeedback(int reservationID, int renterID, int vehicleID, Integer driverID, int driverRating,
	int companyServiceRating, int vehicleConditionRating, int overallExperienceRating, String comments);
	boolean saveToCustomerSupport(int renterID, String username, String issueType, String issueDescription);
	int addVehicle1(Vehicle vehicle);
	List<Vehicle> getAllVehicles1();
	boolean isVehicleAvailable(int vehicleId);
	void addReservation(int renterID, int vehicleID, int companyID, int driverID, Date startDate, Date endDate,
			boolean driverNeed, boolean paymentStatus, double additionalCharges);
	List<Reservation> getAllReservations();
	void deleteReservation(int reservationID);
	boolean isVehicleAvailable(int vehicleID, Date startDate, Date endDate);
	boolean isVehicleAvailableForDates(int vehicleID, Date startDate, Date endDate);
	public int saveReservationm(Reservation reservation);
	boolean updateDriverNeededStatus(int reservationID, boolean driverNeeded);
	int storePaymentDetails(int reservationId, String paymentMethod, double amount, String cardNumber,
			String cardExpiry, String cardCvv, String accountNumber, String bankName, String easypaisaPhoneNumber);
	int createPayment(int reservationId, String paymentMethod, double amount, String cardNumber,
			String cardExpiry, String cardCvv, String accountNumber, String bankName, String easypaisaPhoneNumber);
	double getVehicleDailyAmount(int vehicleID);
	void updatePaymentStatus(int reservationId, boolean paymentStatus);
	void addImage(int vehicleId, String imageUrl);
	String getVehicleNumberPlateByID(int vehicleID);
	String getImageByVehicleId(int vehicleId);
	boolean isVehiclesTableEmpty();
	boolean saveReservation(Reservation reservation);
	int getUserID(String username, String password, String role);
	void updateVehicleAvailability(int vehicleId, boolean available);
}
