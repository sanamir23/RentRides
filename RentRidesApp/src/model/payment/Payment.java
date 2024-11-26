package model.payment;

import java.sql.SQLException;
import java.util.Date;

import database.PersistenceHandler;
import database.PostgreSQL;

public class Payment {
    private int paymentId;
    private int reservationId;
    private String paymentMethod;
    private double amount;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String accountNumber;
    private String bankName;
    private String easypaisaPhoneNumber;
    private Date paymentDate;
	protected static PersistenceHandler persistenceHandler = PostgreSQL.getInstance();

    // Constructor
    public Payment(int paymentId, int reservationId, String paymentMethod, double amount, String cardNumber, 
                   String cardExpiry, String cardCvv, String accountNumber, String bankName, String easypaisaPhoneNumber, Date paymentDate) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCvv = cardCvv;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.easypaisaPhoneNumber = easypaisaPhoneNumber;
        this.paymentDate = paymentDate;
    }

    // Getters and setters for all fields
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getEasypaisaPhoneNumber() {
        return easypaisaPhoneNumber;
    }

    public void setEasypaisaPhoneNumber(String easypaisaPhoneNumber) {
        this.easypaisaPhoneNumber = easypaisaPhoneNumber;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

	public double getVehicleDailyAmount(int vehicleId)  {
		return persistenceHandler.getVehicleDailyAmount(vehicleId);
	}

	public void updatePaymentStatus(int reservationID2, boolean paymentStatus)  {
		persistenceHandler.updatePaymentStatus(reservationID2, paymentStatus);
	}
}
