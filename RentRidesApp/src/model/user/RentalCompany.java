package model.user;

import java.sql.ResultSet;
import java.sql.SQLException;

//import database.PersistenceHandler;
//import database.PostgreSQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RentalCompany extends User {

	private boolean companyStatus;

	public RentalCompany() {
	};

	public RentalCompany(String name, String email, String phone, String address, String username, String password) {
		super(0, name, email, phone, address, username, password);
		this.companyStatus = true;
	}

	public RentalCompany(String username, String password) {
		super(username, password);
	}

	public boolean getCompanyStatus() {
		return companyStatus;
	}

	public void setCompanyStatus(boolean companyStatus) {
		this.companyStatus = companyStatus;
	}

	public ObservableList<User> displayAllCompanies() {
		ObservableList<User> companies = FXCollections.observableArrayList();
		try {
			ResultSet rs = persistenceHandler.displayAllUsers("RentalCompany");

			while (rs.next()) {
				int ID = rs.getInt("companyID");
				String name = rs.getString("companyName");
				String email = rs.getString("email");
				String phone = rs.getString("contactNumber");
				String address = rs.getString("address");
				String username = rs.getString("username");
				String password = rs.getString("password");
				boolean status = rs.getBoolean("companyStatus");

				User user = new RentalCompany(name, email, phone, address, username, password);
				user.setUserID(ID);
				((RentalCompany) user).setCompanyStatus(status);

				companies.add(user);
			}
		} catch (SQLException e) {
			System.out.println("Error fetching renters: " + e.getMessage());
		}

		return companies;
	}

	public void deleteRentalCompany(int ID) {
		persistenceHandler.deleteUser(ID, "RentalCompany");
	}

	public void updateRentalCompany(int ID, String selectedAttribute, String updatedValue) {
		persistenceHandler.updateUser(ID, selectedAttribute, updatedValue, "RentalCompany");
	}

	@Override
	public boolean register(User user) {
		int userID = persistenceHandler.insertUser(user);
		if (userID != 0) {
			user.setUserID(userID);
			return true; // Registration was successful
		}
		return false; // Registration failed
	}

	@Override
	public boolean login(User user) {
		return persistenceHandler.checkLogin(user.getUsername(), user.getPassword(), "RentalCompany");
	}

	@Override
	public boolean verifyLogin() {
		return false;
	}
}