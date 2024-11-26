package model.vehicle;

public class Car extends Vehicle {
    public Car(int id, String name, String registrationNum, String model, double rentPerDay, String location, String features, boolean availability) {
        super(id, name, registrationNum, model, location, rentPerDay, features, availability, "Car");
    }
    
    public Car(String name, String registrationNum, String model, double rentPerDay, String location, String features, boolean availability, int companyID) {
        super(name, registrationNum, model, location, rentPerDay, features, availability, "Car", companyID);
    }
    
    public Car(int vehicleID, String name,String model, String location, String vehicleRegistrationPlate, Double price, String features, String vehicle_type, boolean isAvailable, int companyID) {
     super(vehicleID, name, model, location, vehicleRegistrationPlate, price, features, "Car", isAvailable, companyID);
    }
    
    public Car(int vehicleID, String name,String model, String location, String vehicleRegistrationPlate, Double price, String features, boolean isAvailable, int companyID) {
        super(vehicleID, name, model, location, vehicleRegistrationPlate, price, features, "Car", isAvailable, companyID);
    }
}
