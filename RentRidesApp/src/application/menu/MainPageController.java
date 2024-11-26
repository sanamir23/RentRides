package application.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainPageController {
	
	public String option;
	@FXML
    private Button loginButton;
    @FXML
    private Button registrationButton;

    @FXML
    private void handleLogin(ActionEvent event) {
    	 option = "Login";
    	 openNewStage("/application/menu/ChooseRole.fxml", "Roles", true);
         ((Stage) loginButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleRegistration(ActionEvent event) {
    	option = "Register";
        openNewStage("/application/menu/ChooseRole.fxml", "Roles", true);
        ((Stage) registrationButton.getScene().getWindow()).close();
    }
    
    private void openNewStage(String fxmlFile, String title, boolean isModal) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            ChooseRoleController roleController = loader.getController();
            roleController.setOption(option);

            // Create a new stage (window)
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            // Set the modality if the stage is modal
            if (isModal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }

            // Show the stage
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
