package application.renter;

import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import model.payment.Payment;
import model.payment.PaymentGateway;
import model.reservation.Reservation;
import model.user.User;
import model.vehicle.Vehicle;
import model.vehicle.VehicleAdapter;

import java.time.LocalDate;
import java.util.Date;

import application.menu.RenterMenuController;

public class ProcessPaymentController {

	@FXML
	private Label totalAmountLabel;
	@FXML
	private Button calculateButton;
	@FXML
	private ComboBox<String> paymentMethodComboBox;
	@FXML
	private Button payButton;
	@FXML
	private Label errorLabel;
	@FXML
	private Label successLabel;
	@FXML
	private TextField cardNumberField;
	@FXML
	private TextField cardExpiryField;
	@FXML
	private TextField cardCvvField;
	@FXML
	private TextField accountNumberField;
	@FXML
	private TextField bankNameField;
	@FXML
	private TextField easypaisaPhoneNumberField;

	private Reservation reserved;
	private Vehicle vehicle;
	private double amount = 0.0;
	private VehicleAdapter vehicleS = new VehicleAdapter();
	private User user;
	private PaymentGateway paymentGateway;
	private Payment payment;
	private boolean paymentSuccess = false;

	public void setReservation(Reservation reservation) {
		this.reserved = reservation;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public void setSelectedRenter(User renter) {
		user = renter;
	}

	private double calculateAmount() throws SQLException {
		LocalDate startDate = reserved.getStartDate();
		LocalDate endDate = reserved.getEndDate();
		long duration = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

		double dailyAmount = payment.getVehicleDailyAmount(vehicle.getId());
		return dailyAmount * duration;
	}

	@FXML
	public void initialize() {
		paymentGateway = new PaymentGateway();
		this.payment = new Payment(0, 0, "", 0.0, "", "", "", "", "", "", new Date(System.currentTimeMillis()));

		paymentMethodComboBox.getItems().addAll("Credit Card", "Bank Transfer", "Easypaisa");
		hidePaymentFields();
		addTooltips();

		Platform.runLater(() -> {
			Stage stage = (Stage) payButton.getScene().getWindow();
			stage.setOnCloseRequest(event -> {
				if (!isPaymentCompleted()) {
					showError("Payment was not completed. The vehicle is now available.");
					try {
						vehicleS.updateVehicleAvailability(vehicle.getId(), true);
						reserved.updateMadeReservationFlag(user.getUserID(), false);
						showAlertAndClose("Payment Incomplete", "Payment incomplete, Reservation failed. Try again.");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
		});
	}

	private boolean isPaymentCompleted() {
		return paymentSuccess;
	}

	@FXML
	private void onCalculateButtonClick() {
		try {
			amount = calculateAmount();
			Platform.runLater(() -> {
				totalAmountLabel.setText("PKR " + String.valueOf(amount));

			});
		} catch (SQLException e) {
			showError("Error calculating amount: " + e.getMessage());
		}
	}

	private void addTooltips() {
		cardNumberField.setTooltip(new Tooltip("Enter a 16-digit card number"));
		cardExpiryField.setTooltip(new Tooltip("Enter expiry in MM/YY format"));
		cardCvvField.setTooltip(new Tooltip("Enter a 3-digit CVV code"));
		accountNumberField.setTooltip(new Tooltip("Enter your bank account number (10-12 digits)"));
		bankNameField.setTooltip(new Tooltip("Enter the name of your bank"));
		easypaisaPhoneNumberField
				.setTooltip(new Tooltip("Enter a valid Easypaisa phone number (starts with '03' and 11 digits long)"));
	}

	@FXML
	private void onPaymentMethodSelected() {
		String selectedPaymentMethod = paymentMethodComboBox.getValue();
		hidePaymentFields();

		switch (selectedPaymentMethod) {
		case "Credit Card":
			showCreditCardFields();
			break;
		case "Bank Transfer":
			showBankTransferFields();
			break;
		case "Easypaisa":
			showEasypaisaFields();
			break;
		}
	}

	private void showCreditCardFields() {
		cardNumberField.setVisible(true);
		cardExpiryField.setVisible(true);
		cardCvvField.setVisible(true);
	}

	private void showBankTransferFields() {
		accountNumberField.setVisible(true);
		bankNameField.setVisible(true);
	}

	private void showEasypaisaFields() {
		easypaisaPhoneNumberField.setVisible(true);
	}

	@FXML
	private void handlePayment() throws SQLException {
		errorLabel.setVisible(false);
		successLabel.setVisible(false);

		String selectedPaymentMethod = paymentMethodComboBox.getValue();

		if (selectedPaymentMethod == null) {
			showError("Please select a payment method");
			return;
		}

		paymentSuccess = false;

		try {
			String cardNumber = cardNumberField.getText();
			String cardExpiry = cardExpiryField.getText();
			String cardCvv = cardCvvField.getText();
			String accountNumber = accountNumberField.getText();
			String bankName = bankNameField.getText();
			String easypaisaPhoneNumber = easypaisaPhoneNumberField.getText();

			if (selectedPaymentMethod.equals("Credit Card")
					&& PaymentGateway.validateCreditCard(cardNumber, cardExpiry, cardCvv)) {

				payment = new Payment(0, reserved.getReservationID(), selectedPaymentMethod, amount, cardNumber,
						cardExpiry, cardCvv, "", "", "", new Date(System.currentTimeMillis()));
				paymentSuccess = PaymentGateway.processCreditCardPayment(amount, reserved, vehicle, cardNumber,
						cardExpiry, cardCvv);
			}

			else if (selectedPaymentMethod.equals("Bank Transfer")
					&& PaymentGateway.validateBankTransfer(accountNumber, bankName)) {
				payment = new Payment(0, reserved.getReservationID(), selectedPaymentMethod, amount, "", "", "",
						accountNumber, bankName, "", new Date(System.currentTimeMillis()));
				paymentSuccess = PaymentGateway.processBankTransferPayment(amount, reserved, vehicle, accountNumber,
						bankName);
			}

			else if (selectedPaymentMethod.equals("Easypaisa")
					&& PaymentGateway.validateEasypaisaPayment(easypaisaPhoneNumber)) {
				payment = new Payment(0, reserved.getReservationID(), selectedPaymentMethod, amount, "", "", "", "", "",
						easypaisaPhoneNumber, new Date(System.currentTimeMillis()));
				paymentSuccess = PaymentGateway.processEasypaisaPayment(amount, reserved, vehicle,
						easypaisaPhoneNumber);
			}
		} catch (SQLException e) {
			System.err.println("Error processing payment: " + e.getMessage());
			paymentSuccess = false;
		}

		if (paymentSuccess) {
			showSuccess("Payment Successful using " + selectedPaymentMethod);
			updateReservationPaymentStatus(true);
			clearPaymentFields();
			showAlertAndClose("Payment Successful", "Payment using " + selectedPaymentMethod + " was successful.");
			loadRentrMenuScreen();
		} else {
			showError("Invalid " + selectedPaymentMethod + " details");
		}
	}

	private void hidePaymentFields() {
		cardNumberField.setVisible(false);
		cardExpiryField.setVisible(false);
		cardCvvField.setVisible(false);
		accountNumberField.setVisible(false);
		bankNameField.setVisible(false);
		easypaisaPhoneNumberField.setVisible(false);

		clearPaymentFields();
	}

	private void clearPaymentFields() {
		cardNumberField.clear();
		cardExpiryField.clear();
		cardCvvField.clear();
		accountNumberField.clear();
		bankNameField.clear();
		easypaisaPhoneNumberField.clear();
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	private void showSuccess(String message) {
		successLabel.setText(message);
		successLabel.setVisible(true);
	}

	private void updateReservationPaymentStatus(boolean paymentStatus) {
		payment.updatePaymentStatus(reserved.getReservationID(), paymentStatus);
	}

	private void showAlertAndClose(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();

		Stage stage = (Stage) payButton.getScene().getWindow(); // Use any control from the current scene
		stage.close();
	}

	public void loadRentrMenuScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/menu/RenterMenu.fxml"));
			Parent renterMenuParent = loader.load();
			RenterMenuController controller = loader.getController();
			controller.setRenter(user);
			// Get the current stage and set the new scene
			Stage currentStage = (Stage) payButton.getScene().getWindow();
			Scene renterMenuScene = new Scene(renterMenuParent);

			currentStage.setScene(renterMenuScene);
			currentStage.show(); // Show the new scene

		} catch (Exception e) {
			e.printStackTrace();
			showError("Error loading the renter menu screen: " + e.getMessage());

		}
	}
}
