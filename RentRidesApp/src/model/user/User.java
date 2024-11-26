package model.user;

import database.PersistenceHandler;
import database.PostgreSQL;

public abstract class User {

	protected int userID;
	protected String name;
	protected String email;
	protected String phone;
	protected String address;
	protected String username;
	protected String password;

	protected static final PersistenceHandler persistenceHandler = PostgreSQL.getInstance();

	public User() {
	};

	public User(int userID, String name, String email, String phone, String address, String username, String password) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.username = username;
		this.password = password;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public abstract boolean login(User user);

	public abstract boolean verifyLogin();

	public abstract boolean register(User user);

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}