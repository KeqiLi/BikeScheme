package bikescheme;

public class Bike {
    private String ID;
    private boolean rentStatus;
    private boolean faultStatus;

    public Bike(String bikeID) {
        this.ID = bikeID;
        rentStatus = false;
        faultStatus = false;
    }

    public boolean rentBike() {
        if (rentStatus == false) {
            rentStatus = true;
            return true;
        } else
            return false;

    }

    public boolean returnBike() {
        if (rentStatus == true) {
            rentStatus = false;
            return true;
        } else
            return false;

    }

    public boolean isRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(boolean rentStatus) {
        this.rentStatus = rentStatus;
    }
    
    public void fault() {
        this.faultStatus = true;
    }
    
    public boolean isFault() {
        return faultStatus;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

}