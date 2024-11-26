package model.reservation;

import java.sql.SQLException;

import database.*;

public class Feedback {
	private int feedbackID;
	private int reservationID;
	private int renterID;
	private int vehicleID;
	private int driverID;
	private int driverRating; // Rating for driver
	private int companyServiceRating; // Rating for company service
	private int vehicleConditionRating; // Rating for vehicle condition
	private int overallExperienceRating; // Overall experience rating
	private String comments;
	private static final PersistenceHandler db = PostgreSQL.getInstance();

	public Feedback(int feedbackID, int reservationID, int renterID, int vehicleID, int driverID, int driverRating,
			int companyServiceRating, int vehicleConditionRating, int overallExperienceRating, String comments) {
		this.feedbackID = feedbackID;
		this.reservationID = reservationID;
		this.renterID = renterID;
		this.vehicleID = vehicleID;
		this.driverID = driverID;
		this.driverRating = driverRating;
		this.companyServiceRating = companyServiceRating;
		this.vehicleConditionRating = vehicleConditionRating;
		this.overallExperienceRating = overallExperienceRating;
		this.comments = comments;
	}

	public boolean saveFeedback() {
		return db.saveFeedback(this);
	}

	public int getFeedbackID() {
		return feedbackID;
	}

	public void setFeedbackID(int feedbackID) {
		this.feedbackID = feedbackID;
	}

	public int getReservationID() {
		return reservationID;
	}

	public void setReservationID(int reservationID) {
		this.reservationID = reservationID;
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

	public int getDriverID() {
		return driverID;
	}

	public void setDriverID(int driverID) {
		this.driverID = driverID;
	}

	public int getDriverRating() {
		return driverRating;
	}

	public void setDriverRating(int driverRating) {
		this.driverRating = driverRating;
	}

	public int getCompanyServiceRating() {
		return companyServiceRating;
	}

	public void setCompanyServiceRating(int companyServiceRating) {
		this.companyServiceRating = companyServiceRating;
	}

	public int getVehicleConditionRating() {
		return vehicleConditionRating;
	}

	public void setVehicleConditionRating(int vehicleConditionRating) {
		this.vehicleConditionRating = vehicleConditionRating;
	}

	public int getOverallExperienceRating() {
		return overallExperienceRating;
	}

	public void setOverallExperienceRating(int overallExperienceRating) {
		this.overallExperienceRating = overallExperienceRating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	// Method to display feedback details
	public void displayFeedback() {
		System.out.println("Feedback ID: " + feedbackID);
		System.out.println("Reservation ID: " + reservationID);
		System.out.println("Renter ID: " + renterID);
		System.out.println("Vehicle ID: " + vehicleID);
		System.out.println("Driver ID: " + driverID);
		System.out.println("Driver Rating: " + driverRating);
		System.out.println("Company Service Rating: " + companyServiceRating);
		System.out.println("Vehicle Condition Rating: " + vehicleConditionRating);
		System.out.println("Overall Experience Rating: " + overallExperienceRating);
		System.out.println("Comments: " + comments);
	}
	 public boolean saveFeedback(int reservationID, int renterID, int vehicleID, Integer driverID,
	            Integer driverRating, Integer companyServiceRating, Integer vehicleConditionRating,
	            Integer overallExperienceRating, String comments) throws SQLException {
	    	return db.storeFeedback(reservationID, renterID, vehicleID, driverID, driverRating,
	                companyServiceRating, vehicleConditionRating, overallExperienceRating, comments);
	    }
}