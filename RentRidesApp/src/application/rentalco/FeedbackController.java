package application.rentalco;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import model.reservation.Feedback;
import database.PostgreSQL;

import java.util.List;

public class FeedbackController {

    @FXML
    private VBox feedbackVBox;
    @FXML
    private ScrollPane feedbackScrollPane;
    @FXML
    private Button cancelButton;
    
    private int companyID;
    
    public void setCompanyID(int id) {
    	this.companyID = id;
    	loadFeedbacks();
    }

    private void loadFeedbacks() {
        // Fetch feedbacks from the database
        List<Feedback> feedbacks = PostgreSQL.getFeedbackList(companyID);

        // Clear existing feedback cards
        feedbackVBox.getChildren().clear();
        feedbackVBox.setStyle("-fx-alignment: center_left; -fx-background-color: #000000; -fx-text-fill: white;");
        feedbackVBox.setAlignment(Pos.CENTER_LEFT);

        // Add a card for each feedback
        for (Feedback feedback : feedbacks) {
            HBox feedbackCard = createFeedbackCard(feedback);
            feedbackVBox.setAlignment(Pos.CENTER_LEFT);
            feedbackVBox.getChildren().add(feedbackCard);
        }
    }
    
    private HBox createFeedbackCard(Feedback feedback) {
        // Create a container for the card
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: #70cd98; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: #000000; -fx-alignment: center_left; -fx-text-fill: white;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Display feedback ratings
        VBox detailsBox = new VBox(5);
        String renterUsername = PostgreSQL.getRenterUsername(feedback.getRenterID());
        Text renterUsernameText = new Text("Renter Username: " + renterUsername);
        renterUsernameText.setFill(Color.web("#f2fff7"));
        renterUsernameText.setStyle("-fx-font-weight: bold;");
        Text driverRatingText = new Text("Driver Rating: " + feedback.getDriverRating());
        driverRatingText.setFill(Color.web("#f2fff7"));
        Text companyServiceRatingText = new Text("Company Service Rating: " + feedback.getCompanyServiceRating());
        companyServiceRatingText.setFill(Color.web("#f2fff7"));
        Text vehicleConditionRatingText = new Text("Vehicle Condition Rating: " + feedback.getVehicleConditionRating());
        vehicleConditionRatingText.setFill(Color.web("#f2fff7"));
        Text overallExperienceText = new Text("Overall Experience Rating: " + feedback.getOverallExperienceRating());
        overallExperienceText.setFill(Color.web("#f2fff7"));
        Text comments = new Text("Comments: " + feedback.getComments());
        comments.setFill(Color.web("#f2fff7"));
        
        detailsBox.getChildren().addAll(renterUsernameText, driverRatingText, companyServiceRatingText, vehicleConditionRatingText, overallExperienceText, comments);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        // Add elements to the card
        card.getChildren().addAll(detailsBox);

        return card;
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
