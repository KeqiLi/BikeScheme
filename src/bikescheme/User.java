package bikescheme;
import java.util.ArrayList;
import java.util.Calendar;

public class User {

	private String userAccount;
	private String keyID;
	private String card;
	private ArrayList<Trip> trips;
	private Trip currentTrip;
	
	public User(String userAccount, String keyNumber, String card) {
	    this.userAccount = userAccount;
	    this.keyID = keyNumber;
	    this.card = card;
	    this.trips = new ArrayList<Trip>();
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getKeyNumber() {
		return keyID;
	}
	public void setKeyID(String keyID) {
		this.keyID = keyID;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	
	/**
	 * The user starts a trip
	 * 
	 * @param station
	 */
	public void startTrip(String station){
	    currentTrip = new Trip(station);
	}
	
	/**
	 * The user finished the current trip
	 * 
	 * @param station
	 */
	public void endTrip(String station){
	    currentTrip.endTrip(station);
	    trips.add(currentTrip);
	}
	
	/**
	 * Return this user's trips of the day
	 * 
	 * @return todayTrips
	 */
	public ArrayList<Trip> getTrips(){
	    ArrayList<Trip> todayTrips = new ArrayList<Trip>();
	    for (Trip t: trips) {
	        if (Clock.getInstance().getDateAndTimeAsCalendar().get(Calendar.DAY_OF_MONTH) 
	                == t.getDay() ) {
	            todayTrips.add(t);
	        }
	    }
	    
	    return todayTrips;
	}
	

}
