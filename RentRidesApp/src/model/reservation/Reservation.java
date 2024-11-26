package model.reservation;

import java.time.*;
import database.PersistenceHandler;
import database.PostgreSQL;
import javafx.collections.ObservableList;
import javafx.collections.*;
import java.sql.*;

public class Reservation {
	private int reservationID;
	private int renterID;
	private int vehicleID;
	private int driverID;
	private int rentalID;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean isDriver;
	private boolean paymentStatus;
	private double additionalCharges;

	private static final PersistenceHandler db = PostgreSQL.getInstance();
	
	public Reservation() {}

	public Reservation(int reservationID, int renterID, int vehicleID, int rentalID, int driverID, LocalDate startDate,
			LocalDate endDate, boolean paymentStatus, double additionalChrgs, boolean isDriver) {
		this.reservationID = reservationID;
		this.renterID = renterID;
		this.vehicleID = vehicleID;
		this.driverID = driverID;
		this.rentalID = rentalID;
		this.startDate = startDate;
		this.isDriver = isDriver;
		this.endDate = endDate;
		this.paymentStatus = paymentStatus;
		this.additionalCharges = additionalChrgs;
	}

	public Reservation(int renterID, int vehicleID, int rentalID, int driverID, LocalDate startDate, LocalDate endDate,
			boolean isDriver, boolean paymentStatus, double additionalChrgs) {
		this.reservationID = 0;
		this.renterID = renterID;
		this.vehicleID = vehicleID;
		this.driverID = driverID;
		this.rentalID = rentalID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isDriver = isDriver;
		this.paymentStatus = paymentStatus;
		this.additionalCharges = additionalChrgs;
	}

	public Reservation(int renterID, int vehicleID, int rentalID, LocalDate startDate, LocalDate endDate,
			boolean isDriver, boolean paymentStatus, double additionalChrgs) {
		this.reservationID = 0;
		this.renterID = renterID;
		this.vehicleID = vehicleID;
		this.driverID = -1;
		this.rentalID = rentalID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isDriver = isDriver;
		this.paymentStatus = paymentStatus;
		this.additionalCharges = additionalChrgs;
	}
	
	public Reservation(int renterID, int vehicleID, int rentalID, LocalDate startDate, LocalDate endDate,
			boolean paymentStatus, double additionalChrgs, boolean isDriver) {
		this.reservationID = 0;
		this.renterID = renterID;
		this.vehicleID = vehicleID;
		this.driverID = -1;
		this.rentalID = rentalID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isDriver = isDriver;
		this.paymentStatus = paymentStatus;
		this.additionalCharges = additionalChrgs;
	}

	public int getReservationID() {
		return reservationID;
	}

	public boolean updateReservation() {
		return db.updateReservation(this);
	}

	public boolean deleteReservation() {
		return db.deleteReservation(this);
	}

	public boolean saveReservation() {
		return db.saveReservation(this);
	}

	public void setReservationID(int reservationID) {
		this.reservationID = reservationID;
	}
	
	public ObservableList<Reservation> fetchReservationsForDriver(int ID)
	{
		ObservableList<Reservation> reservationList = FXCollections.observableArrayList();

	    ResultSet rs = db.displayReservationsForDriver(ID);
	    
	    try {
	        while (rs != null && rs.next()) {
	            Reservation reservation = new Reservation();
	            reservation.setReservationID(rs.getInt("reservationID"));
	            reservation.setRenterID(rs.getInt("renterID"));
	            reservation.setVehicleID(rs.getInt("vehicleID"));
	            reservation.setDriverID(rs.getInt("driverID"));
	            reservation.setRentalID(rs.getInt("companyID")); 
	            reservation.setStartDate(rs.getDate("startDate").toLocalDate()); 
	            reservation.setEndDate(rs.getDate("endDate").toLocalDate());     
	            reservation.setPaymentStatus(rs.getBoolean("paymentStatus")); 
	            reservation.setAdditionalCharges(rs.getDouble("additionalCharges")); // Fetching additionalCharges
	            // Add the populated reservation object to the list
	            reservationList.add(reservation);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error processing customer support data: " + e.getMessage());
	    }
	    
	    return reservationList;

	}

	public int getDriverID() {
		return driverID;
	}

	public void setRentalID(int rentalID) {
		this.rentalID = rentalID;
	}

	public int getRentalID() {
		return rentalID;
	}

	public boolean ifDriverNeeded() {
		return isDriver;
	}

	public void setIsDriver(boolean s) {
		this.isDriver = s;
	}

	public void setDriverID(int driverID) {
		this.driverID = driverID;
	}

	public int getRenterID() {
		return renterID;
	}

	public void setRenterID(int renterID) {
		this.renterID = renterID;
	}

	public int getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(int vehicleID) {
		this.vehicleID = vehicleID;
	}

	public double getAdditionalCharges() {
		return additionalCharges;
	}

	public void setAdditionalCharges(double charges) {
		this.additionalCharges = charges;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public boolean isPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	public int saveReservationInTable(Reservation reservation) throws SQLException {
		return db.saveReservationm(reservation);
	}

	public void updateDriverNeedStatus(int reservationId2, boolean b) {
		db.updateDriverNeededStatus(reservationId2, b);
	}

	public void updateMadeReservationFlag(int renterID2, boolean b) {
		db.updateMadeReservation(renterID2, b);
		
	}

	public boolean checkMadeReservation(int renterID2) throws SQLException {
		return db.checkMadeReservation(renterID2);
	}

	public Reservation getActiveReservationByRenterID(int renterID2) throws SQLException {
		return db.getActiveReservationByRenterID(renterID2);
	}

	public double getPaymentAmountByReservationID(int reservationID2) throws SQLException {
		return db.getPaymentAmountByReservationID(reservationID2);
	}

	public void displayReservationDetails() {
		System.out.println("Reservation ID: " + reservationID);
		System.out.println("Renter ID: " + renterID);
		System.out.println("Vehicle ID: " + vehicleID);
		System.out.println("Start Date: " + startDate);
		System.out.println("End Date: " + endDate);
		System.out.println("Payment Status: " + (paymentStatus ? "Paid" : "Unpaid"));
	}

	public int getCompanyID() {
		return rentalID;
	}

	public boolean isDriver() {
		return isDriver;
	}

	public double getAdditionalPrice() {
		return additionalCharges;
	}
	
	public int getRenterIDFromReservation(int reservationID2) throws SQLException {
		return db.getRenterIDFromReservation(reservationID2);
	}

	public void updateReservationInDatabase(int reservationID2, int renterID2, double newPrice) throws SQLException {
		db.updateReservationInDatabase(reservationID2, renterID2, newPrice);
	}
	public void updateMadeReservationToFalse(int renterID2) throws SQLException {
		db.updateMadeReservationToFalse(renterID2);
	}
}
