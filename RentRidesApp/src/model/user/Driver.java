package model.user;

public class Driver extends User {

	protected String licenseNum;
	protected boolean isAvailable;
	protected int companyID;
	protected String companyUsername;
	protected String status;

	public Driver() {
	};

	public Driver(String name, String email, String phone, String address, String username, String password,
			String licenseNum, boolean isAvailable, String companyUsername, String status) {
		super(0, name, email, phone, address, username, password);
		this.isAvailable = isAvailable;
		this.licenseNum = licenseNum;
		this.companyUsername = companyUsername;
		this.status = status;
	}
	
	public Driver(String name, String email, String phone, String address, String username, String password,
			String licenseNum, String companyUsername, String status) {
		super(0, name, email, phone, address, username, password);
		this.isAvailable = true;
		this.licenseNum = licenseNum;
		this.companyUsername = companyUsername;
		this.status = status;
	}
	
	public Driver(int driverID, String name, String email, String phone, String address, String username, String password,
			String licenseNum, boolean isAvailable, int companyID, String status) {
		super(driverID, name, email, phone, address, username, password);
		this.isAvailable = isAvailable;
		this.licenseNum = licenseNum;
		this.companyID = companyID;
		this.status = status;
	}
	
	public Driver(String name, String email, String phone, String address, String username, String password, String licenseNum) {
		super(0, name, email, phone, address, username, password);
		this.isAvailable = true;
		this.licenseNum = licenseNum;
	}
	
	public void updateDriverDetails(int ID, String selectedAttribute,String updatedValue) {
		persistenceHandler.updateUser(ID, selectedAttribute, updatedValue, "Driver");
	}

	public Driver(String username, String password) {
		super(username, password);
	}

	public String getLicenseNum() {
		return licenseNum;
	}

	public void setLicenseNum(String licenseNum) {
		this.licenseNum = licenseNum;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
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

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public String getCompanyUsername() {
		return companyUsername;
	}

	public void setCompanyUsername(String companyUsername) {
		this.companyUsername = companyUsername;
	}

	@Override
	public boolean login(User user) {
		return persistenceHandler.checkLogin(user.getUsername(), user.getPassword(), "Driver");
	}

	@Override
	public boolean verifyLogin() {
		return false;
	}

	public int getDriverID(String username) {
		int driverID = persistenceHandler.getDriverIDFromDB(username);
		if (driverID != 0) {
			return driverID;
		}
		return 0;
	}

	public Driver getDriverDetails(int driverID) {
	 return persistenceHandler.getDriver(driverID);
		
	}
}