package model.payment;

import java.sql.SQLException;
import database.PersistenceHandler;
import database.PostgreSQL;
import model.reservation.Reservation;
import model.vehicle.Vehicle;

public class PaymentGateway {
	protected static PersistenceHandler persistenceHandler = PostgreSQL.getInstance();

    public PaymentGateway() {
        // No need to initialize dbController here anymore.
    }

    public static boolean processCreditCardPayment(double amount, Reservation reserved, Vehicle vehicle, 
                                                   String cardNumber, String expiry, String cvv) throws SQLException {
        boolean paymentSuccess = validateCreditCard(cardNumber, expiry, cvv);
        if (paymentSuccess) {
            paymentSuccess = persistenceHandler.storePaymentDetails(reserved.getReservationID(), "Credit Card", amount,
                    cardNumber, expiry, cvv, null, null, null) > 0;
        }
        return paymentSuccess;
    }

    public static boolean processBankTransferPayment(double amount, Reservation reserved, Vehicle vehicle, 
                                               String accountNumber, String bankName) throws SQLException {
        boolean paymentSuccess = validateBankTransfer(accountNumber, bankName);
        if (paymentSuccess) {
            paymentSuccess = persistenceHandler.storePaymentDetails(reserved.getReservationID(), "Bank Transfer", amount,
                    null, null, null, accountNumber, bankName, null) > 0;
        }
        return paymentSuccess;
    }

    public static boolean processEasypaisaPayment(double amount, Reservation reserved, Vehicle vehicle, 
                                            String phoneNumber) throws SQLException {
        boolean paymentSuccess = validateEasypaisaPayment(phoneNumber);
        if (paymentSuccess) {
            paymentSuccess = persistenceHandler.storePaymentDetails(reserved.getReservationID(), "Easypaisa", amount,
                    null, null, null, null, null, phoneNumber) > 0;
        }
        return paymentSuccess;
    }

    // Validation Methods

    public static boolean validateCreditCard(String cardNumber, String expiry, String cvv) {
        return cardNumber.length() == 16 && expiry.length() == 5 && cvv.length() == 3;
    }

    public static boolean validateBankTransfer(String accountNumber, String bankName) {
        return accountNumber.length() >= 10 && bankName.length() > 0;
    }

    public static boolean validateEasypaisaPayment(String phoneNumber) {
        return phoneNumber.length() == 11 && phoneNumber.startsWith("03");
    }
}
