package model.user;

public class Admin extends User {

	public Admin(String name, String email, String phone, String address, String username, String password) {
		super(0, name, email, phone, address, username, password);
	}

	public Admin(String username, String password) {
		super(username, password);
	}

	@Override
	public boolean register(User user) {
		int userID = persistenceHandler.insertUser(user);
		if (userID != 0) {
			user.setUserID(userID);
			return true; 
		}
		return false;
	}

	@Override
	public boolean login(User user) {
	    return persistenceHandler.checkLogin(user.getUsername(), user.getPassword(), "Admin");
	}

	@Override
	public boolean verifyLogin() {
		return false;
	}
}