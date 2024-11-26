package model.reservation;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import database.PersistenceHandler;
import database.PostgreSQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerSupport {
	private int supportID;
	private int renterID;
	private String username;
	private String issueType;
	private String issueDescription;
	private String status;
	private String response;
	private Timestamp createdAt;

	private static final PersistenceHandler persistenceHandler = PostgreSQL.getInstance();

	public CustomerSupport() {
	};
	
    public CustomerSupport(String username, String issueType, String issueDescription) {
        this.username=username;
        this.issueType = issueType;
        this.issueDescription = issueDescription;
    }
	
    public String getUsername() {
    	return username;
    }
    public void setUsername(String username) {
    	this.username=username;
    }
	public int getSupportID() {
		return supportID;
	}

	public void setSupportID(int supportID) {
		this.supportID = supportID;
	}

	public int getRenterID() {
		return renterID;
	}

	public void setRenterID(int renterID) {
		this.renterID = renterID;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getIssueDescription() {
		return issueDescription;
	}

	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getCreatedAt() {
		if (this.createdAt != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.format(this.createdAt);
		}
		return null;
	}

	public void setCreatedAt(Timestamp timestamp) {
		this.createdAt = timestamp;
	}
	 // Method to save the customer support details to the database
    public boolean saveToDatabase(int renterID,String username, String issueType,String issueDescription) {
        
        return persistenceHandler.saveToCustomerSupport(renterID,username, issueType, issueDescription);
    }
	public ObservableList<CustomerSupport> fetchCustomerSupportData() {
		ObservableList<CustomerSupport> supportList = FXCollections.observableArrayList();

		ResultSet rs = persistenceHandler.displayAllCustomerSupport();
		try {
			while (rs != null && rs.next()) {
				CustomerSupport support = new CustomerSupport();
				support.setSupportID(rs.getInt("supportID"));
				support.setRenterID(rs.getInt("renterID"));
				support.setIssueType(rs.getString("issueType"));
				support.setIssueDescription(rs.getString("issueDescription"));
				support.setStatus(rs.getString("status"));
				support.setResponse(rs.getString("response"));
				support.setCreatedAt(rs.getTimestamp("createdAt"));

				supportList.add(support);
			}
		} catch (SQLException e) {
			System.out.println("Error processing customer support data: " + e.getMessage());
		}

		return supportList;
	}

	public void updateSupport(int supportID, String response, String status) {
		persistenceHandler.updateCustomerSupport(supportID, response, status);
	}

	public void deleteCustomerSupport(int ID) {
		persistenceHandler.deleteSupport(ID);
	}
}
