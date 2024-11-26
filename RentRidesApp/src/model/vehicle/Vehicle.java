package model.vehicle;

import database.PersistenceHandler;
import database.PostgreSQL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Vehicle {
    private int id; 
    private String name;
    private String model;
    private String location; 
    private String registrationNum;
    private double rentPerDay;
    private String features;
    private String vehicleType; 
    private boolean isAvailable;
    private Image vehicleImage; 
    private int companyID;
    private static final PersistenceHandler db = PostgreSQL.getInstance();

    public Vehicle(int id, String name, String registrationNum, String model, String location, double rentPerDay, String features, boolean availability, String vehicleType) {
        this.id = id;
        this.name = name;
        this.registrationNum = registrationNum;
        this.model = model;
        this.location = location;
        this.rentPerDay = rentPerDay;
        this.features = features;
        this.vehicleType = vehicleType;
        this.isAvailable = availability;
    }
    public Vehicle(int vehicleID, String name, String model, String location, String vehicleRegistrationPlate,Double price,String features, String vehicle_type, Boolean is_available, int companyID) {
			 this.id = vehicleID;
			 this.name=name;
			 this.model = model;
			 this.location = location;
			 this.registrationNum = vehicleRegistrationPlate;
			 this.rentPerDay = price;
			 this.features=features;
			 this.vehicleType=vehicle_type;
			 this.isAvailable=is_available;
			 this.companyID = companyID;
	} 

    public Vehicle(String name, String registrationNum, String model, String location, double rentPerDay, String features, boolean availability, String vehicleType, int companyID) {
    	 this.id = 0;
		 this.name=name;
		 this.model = model;
		 this.location = location;
		 this.registrationNum = registrationNum;
		 this.rentPerDay = rentPerDay;
		 this.features = features;
		 this.vehicleType = vehicleType;
		 this.isAvailable = availability;
		 this.companyID = companyID;
    }

    public boolean saveVehicle() {
        return db.addVehicle(this);
    }

    public boolean deleteVehicle() {
        return db.deleteVehicle(this);
    }

    public boolean updateVehicle() {
        return db.updateVehicle(this);
    }
    
    public void saveImageToDB() {
    	db.addImage(id, this.vehicleImage.getUrl());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { 
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }
    
    public void setImageUrl(String url) {
        if (url != null && !url.isEmpty()) {
            this.vehicleImage = new Image(url);
        } else {
            this.vehicleImage = new Image("file:/C:/Users/Sana%20Mir/Desktop/SDA_Project/RentRidesApp/resources/vehicle_images/civic.jpg"); // Provide a default image path here
        }
    }
    
    public String getImageUrl() {
    	return this.vehicleImage.getUrl();
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegistrationNum() {
        return registrationNum;
    }

    public void setRegistrationNum(String registrationNum) {
        this.registrationNum = registrationNum;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    public void setRentPerDay(double rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Image getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String imagePath) { 
        this.vehicleImage = new Image(imagePath);
    }

    public ImageView getVehicleImageView() {
        if (vehicleImage != null) {
            ImageView imageView = new ImageView(vehicleImage);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            return imageView;
        } else {
            return null;
        }
    }

    public String getDescription() {
        return features;
    }

	public String getVehicleRegistrationPlate() {
		// TODO Auto-generated method stub
		return registrationNum;
	}

	public double getPrice() {
		// TODO Auto-generated method stub
		return rentPerDay;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return vehicleType;
	}

	public int getCompanyID() {
		// TODO Auto-generated method stub
		return companyID;
	}
	public void setVehicleID(int vehicleId) {
		this.id=vehicleId;
	}
}
