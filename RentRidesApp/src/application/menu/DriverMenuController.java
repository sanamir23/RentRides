package application.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import application.driver.ViewScheduleController;
import application.driver.EditDriverProfileController;

public class DriverMenuController {
	
	private int driverID;	
	@FXML
	private Button viewReservationButton;
	@FXML
	private Button editProfileButton;
	
	
	 @FXML
	    public void handleButtonAction(ActionEvent event) {
	        Button clickedButton = (Button) event.getSource();
	        String fxmlFile = null;
	        String title = "";

	        if (clickedButton == viewReservationButton) {
	            fxmlFile = "/application/driver/ViewSchedule.fxml";
	            title = "View Reservation";
	            openNewStage(fxmlFile, title, ViewScheduleController.class);
	        } 
	        
	        else if (clickedButton == editProfileButton) {
	            fxmlFile = "/application/driver/EditDriverProfile.fxml";
	            title = "Edit Profile";
	            openNewStage(fxmlFile, title, EditDriverProfileController.class);
	        }
	    }

	    private <T> void openNewStage(String fxmlFile, String title, Class<T> controllerClass) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
	            Parent root = loader.load();
	            
	            // Get the controller and set the driver ID
	            T controller = loader.getController();
	            if (controller instanceof ViewScheduleController) {
	                ((ViewScheduleController) controller).setData(this.driverID);
	            } 
	            
	            else if (controller instanceof EditDriverProfileController) {
	                ((EditDriverProfileController) controller).setData(this.driverID);
	            }

	            // Open the new stage
	            Stage stage = new Stage();
	            stage.setTitle(title);
	            stage.setScene(new Scene(root));
	            stage.show();

	            // Close the current dashboard window if needed
	            ((Stage) viewReservationButton.getScene().getWindow()).close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	public void setData(int ID)
	{
		this.driverID = ID;
	}
}
