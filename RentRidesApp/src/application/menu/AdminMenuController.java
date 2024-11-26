package application.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class AdminMenuController {
	protected String role;
	
	@FXML
	private Button manageRenterButton;
	
	@FXML
	private Button manageRentalCompanyButton;
	
	@FXML
	private Button customerSupportButton;
	
	
	@FXML
	public void initialize()
	{
		
	}
	
	@FXML 
    public void handleButtonAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String fxmlFile = null;
        String title = "";

        if (clickedButton == manageRenterButton) {
            fxmlFile = "/application/admin/ManageRenter.fxml";
            title = "Manage Renter";
        } else if (clickedButton == manageRentalCompanyButton) {
            fxmlFile = "/application/admin/ManageRentalCompany.fxml";
            title = "Manage Rental Company";
        } else if (clickedButton == customerSupportButton) {
            fxmlFile = "/application/admin/ManageCustomerSupport.fxml";
            title = "Customer Support";
        }

        if (fxmlFile != null) {
            openNewStage(fxmlFile, title);
        }
    }

    private void openNewStage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current Admin Panel window if necessary
            ((Stage) manageRenterButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
