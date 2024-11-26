package model.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.menu.*;
public class Renter extends User {
	
	protected boolean madeReservation;
    LoginController loginController = new LoginController();

	public Renter(String name, String email, String phone, String address, String username, String password, boolean madeReservation) {
		super(0, name, email, phone, address, username, password);
		this.madeReservation = madeReservation;
	}

	public Renter(String username, String password) {
		super(username, password);
	}

	public Renter() {
	}

	@Override
	public boolean register(User user) {
		int userID = persistenceHandler.insertUser(user);
		if (userID != 0) {
			user.setUserID(userID);
			return true; 
		}
		loginController.setUser(user); 

		return false;
	}

	
	@Override
	public boolean login(User user) {
	    boolean isLoginSuccessful = persistenceHandler.checkLogin(user.getUsername(), user.getPassword(), "Renter");
	    if (isLoginSuccessful) {
	        int userID = persistenceHandler.getUserID(user.getUsername(), user.getPassword(), "Renter");
	        System.out.print("PRINTING USERID IN RENTER.JAVA: "+userID+"\n");
	        user.setUserID(userID); 
	        if (loginController != null) {
		        System.out.print("setting user in login/renter \n");
                loginController.setUser(user); 
            }
	    }
	    return isLoginSuccessful;
	}


	@Override
	public boolean verifyLogin() {
		return false;
	}

	public ObservableList<User> displayAllRenters() {
		ObservableList<User> renters = FXCollections.observableArrayList();
		try {
			ResultSet rs = persistenceHandler.displayAllUsers("Renter");

			while (rs.next()) {
				int ID = rs.getInt("renterID");
				String name = rs.getString("renterName");
				String email = rs.getString("email");
				String phone = rs.getString("phone");
				String address = rs.getString("address");
				String username = rs.getString("username");
				String password = rs.getString("password");

				User user = new Renter(name, email, phone, address, username, password,madeReservation);
				user.setUserID(ID);

				renters.add(user);
			}
		} catch (SQLException e) {
			System.out.println("Error fetching renters: " + e.getMessage());
		}

		return renters; // Return the list of renters
	}

	public void deleteRenter(int ID) {
		persistenceHandler.deleteUser(ID, "Renter");
	}

	public void updateRenter(int ID, String selectedAttribute, String updatedValue) {
		persistenceHandler.updateUser(ID, selectedAttribute, updatedValue, "Renter");
	}
	
	public void setMadeReservation(boolean isReserved) {
		madeReservation=isReserved;
	}
	public boolean getMadeReservation() {
		return madeReservation;
	}
	
	public String getRenterNameByID(int renterID) {
		return persistenceHandler.getRenterNameByID(renterID);
	}
	
	public String getVehicleNameByID(int vehicleID) {
		return persistenceHandler.getVehicleNameByID(vehicleID);
	}
}