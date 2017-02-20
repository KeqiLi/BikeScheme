/**
 * 
 */
package bikescheme;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * 
 * Hub system.
 *
 * 
 * @author pbj 
 *
 */
public class Hub implements AddDStationObserver, HubInterface, ShowStatsObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private HubTerminal terminal;
    private HubDisplay display;
    private Map<String, DStation> dockingStationMap;
    // Create a map that links user to a specific key <String keyID, User user>
    private Map<String, User> keyToUserMap;
    // Create a map that links bike to a specific key <String bikeID, String
    // keyID>
    private Map<String, String> bikeToKeyMap;
    // Create a map that links a bike to a specific key <String bikeID, String
    // keyID>
    private Map<String, Bike> bikeMap;
    // Create an arraylist of fault bike;
    private ArrayList<Bike> faultBikes;
    // Create a bank server and set a date to start charging from 23:59 1 Jan 1970.
    private BankServer bankServer;
    public static Date chargeStart = new Date((24 * 60 - 1) * 60 * 1000L);

    /**
     * 
     * Construct a hub system with an operator terminal, a wall display and
     * connections to a number of docking stations (initially 0).
     * 
     * Schedule update of the hub wall display every 5 minutes with docking
     * station occupancy data.
     * 
     * @param instanceName
     */
    public Hub() {
        Clock.createInstance();
        
        // Construct and make connections with interface devices
        terminal = new HubTerminal("ht");
        terminal.setObserver(this);
        terminal.setShowStatsObserver(this);
        display = new HubDisplay("hd");
        dockingStationMap = new HashMap<String, DStation>();
        bikeMap = new HashMap<String, Bike>();
        bikeToKeyMap = new HashMap<String, String>();
        keyToUserMap = new HashMap<String, User>();
        faultBikes = new ArrayList<Bike>();
        bankServer = new BankServer("bsv");
        
        // Default user, staff, who use keys identified as "admin".
        // "****" indicates no card detail;
        newUser("STAFF", "admin", "****"); 

        // Schedule timed notification for generating updates of
        // hub display.
        // Every 5 mins it shows the occupancy on display.
        Clock.getInstance().scheduleNotification(new TimedNotificationObserver() {

            /**
             * Generate display of station occupancy data.
             */
            @Override
            public void processTimedNotification() {
                logger.fine("Refreshing Display");
                ArrayList<String> occupancyList = showOccupancy(0, 0);
                display.showOccupancy(occupancyList);
            }

        }, Clock.getStartDate(), 0, 5);
        
        
        // Schedule timed notification for charging users.
        // Every 24 hrs it charges the users.
        Clock.getInstance().scheduleNotification(new TimedNotificationObserver() {
            /**
             * 
             * 
             */
            @Override
            public void processTimedNotification() {
                logger.fine("Charging User");
                
                bankServer.chargeUsers(keyToUserMap.values(), computeDebt(keyToUserMap.values()));
            }
        }, chargeStart, 24, 0);

    }

    public void setDistributor(EventDistributor d) {

        // The clock device is connected to the EventDistributor here, even
        // though the clock object is not constructed here,
        // as no distributor is available to the Clock constructor.
        Clock.getInstance().addDistributorLinks(d);
        terminal.addDistributorLinks(d);
    }

    public void setCollector(EventCollector c) {
        display.setCollector(c);
        terminal.setCollector(c);
        bankServer.setCollector(c);
    }
    
    /**
     * Show the occupancy on the large display in Hub.
     * 
     * This function generates the output data needed for the HubDisplay.
     * 
     */
    public ArrayList<String> showOccupancy(int east, int north){
    ArrayList<String> occupancyList = new ArrayList<String>();
    // "DSName","East","North","Status","#Occupied","#DPoints"
    for (DStation a : dockingStationMap.values()) {
        float occupancy = ((float) a.getOccupiedDPoints() / a.getNoDPoints());
        String status = "OK";
        if (occupancy >= 0.85)
            status = "HIGH";
        if (occupancy <= 0.15)
            status = "LOW";
        occupancyList.add(a.getInstanceName());
        occupancyList.add(Integer.toString(a.getEastPos()-east));
        occupancyList.add(Integer.toString(a.getNorthPos()-north));
        occupancyList.add(status);
        occupancyList.add(Integer.toString(a.getOccupiedDPoints()));
        occupancyList.add(Integer.toString(a.getNoDPoints()));
    }
    return occupancyList;
    }
    
    /**
     * Find the freepoints of DStations near a given position.
     * 
     * The position is set by its eastPos and northPos.
     * 
     */
    @Override
    public ArrayList<String> findFreePoints(DStation s){
        logger.fine("");
        return showOccupancy(s.getEastPos(), s.getNorthPos());
    }
    
    /**
     * This function adds a new DStation to the system and maps its
     * instanceName to it for convenience.
     * 
     */
    @Override
    public void addDStation(String instanceName, int eastPos, int northPos, int numPoints) {
        logger.fine("");

        DStation newDStation = new DStation(instanceName, eastPos, northPos, numPoints);
        dockingStationMap.put(instanceName, newDStation);

        // Now connect up DStation to event distributor and collector.

        EventDistributor d = terminal.getDistributor();
        EventCollector c = display.getCollector();

        newDStation.setDistributor(d);
        newDStation.setCollector(c);

        newDStation.setHub(this);
        ;

    }

    /**
     * This function supports both "hire bike" and "remove bike" use cases.
     * 
     * It simulates a bike being taken out by a key.
     * 
     * @param bikeID
     */
    @Override
    public void bikeTakeOut(String keyID, String bikeID, DPoint d) {
        logger.fine("");
        if (keyID == "admin"){
            logger.fine(bikeID + " is removed.");
            bikeMap.remove(bikeID);
        } else if (this.keyToUserMap.containsKey(keyID)) {
            bikeToKeyMap.put(bikeID, keyID);
            logger.fine(bikeID + " is linked to " + bikeToKeyMap.get(bikeID));
            bikeMap.get(bikeID).rentBike();
            keyToUserMap.get(keyID).startTrip(d.getStationName());
        } else {
            logger.warning(keyID + " Is Not A Valid Key!");
        }
    }

    /**
     * This function supports both "hire bike" and "remove bike" use cases.
     * 
     * 
     */
    public void bikeDocked(String bikeID, DPoint d) {
        logger.fine("");
        if (!this.bikeMap.containsKey(bikeID)) {
            Bike newBike = new Bike(bikeID);
            bikeMap.put(bikeID, newBike);
        } else {
            bikeMap.get(bikeID).returnBike();
            keyToUserMap.get(bikeToKeyMap.get(bikeID)).endTrip(d.getStationName());
            bikeToKeyMap.remove(bikeID);
        }
    }

    /**
     * This adds a new user to the system.
     * 
     */
    public void newUser(String info, String keyID, String card) {
        User u = new User(info, keyID, card);
        logger.fine(keyID);
        this.keyToUserMap.put(keyID, u);
    }

    /**
     * This function generates user activity for a user with keyID
     * 
     * @param keyID
     * @return
     */
    public ArrayList<String> userActivity(String keyID) {
        ArrayList<String> ac = new ArrayList<String>();
        User u = this.keyToUserMap.get(keyID);
        logger.fine(keyToUserMap.get(keyID).getKeyNumber());
        for (Trip t : u.getTrips()) {
            ac.add(Clock.format(t.getStart()).split(" ")[1]);
            ac.add(t.getStartStation());
            ac.add(t.getEndStation());
            ac.add(Integer.toString(t.getLength()));
        }
        return ac;
    }
    
    /**
     * 
     */
    public void showStatsReceived(){
        logger.fine("");
        
        ArrayList<String> stat = new ArrayList<String>();
        stat.add("Property");
        stat.add("Value/Stat");
        stat.add("#Journeys Today");
        int journeyNo = 0, journeyTimeSum = 0;
        for (User u : keyToUserMap.values()) {
            journeyNo += u.getTrips().size();
            for (Trip t : u.getTrips()) {
                journeyTimeSum += t.getLength();
            }
        }
        stat.add(Integer.toString(journeyNo));
        stat.add("#Users Registered");
        stat.add(Integer.toString(keyToUserMap.size()-1)); // User size -1 (the admin).
        stat.add("Average Journey Time");
        stat.add(Float.toString((float) journeyTimeSum/journeyNo));
        
        terminal.showStats(stat);
    }


    /**
     * 
     */
    public boolean containsBike(String bikeID) {
        if (bikeMap.containsKey(bikeID))
            return true;
        else
            return false;
    }

    
    /**
     * 
     * @param dp
     */
    public void fault(DPoint dp) {
        logger.fine("");
        
        faultBikes.add(bikeMap.get(dp.getBikeID()));
    }
    
    /**
     * This function computes the debts of all provided users.
     * 
     * @param users
     */
    public ArrayList<String> computeDebt(Collection<User> users) {
        // Computing each user's debt amount.
        ArrayList<String> debtData = new ArrayList<String>();
        for (User user : users) {
            int debt = 0, timeSum = 0;
            // 123456
            for (Trip t : user.getTrips()) {
                int time = t.getLength();
                timeSum += time;
                if (time > 0) {
                    debt += 1;
                    if (time > 30) {
                        if ((time - 30) % 30 > 0)
                            debt += ((time - 30) / 30) * 2;
                        else
                            debt += ((time - 30) / 30) * 2;
                    }
                }
            }
            // Add to print out list
            if (debt > 0) {
                //"UserName", "KeyID", "#Trips", "TimeSum", "DebitAmount" 
                String[] userDebt = 
                         {user.getUserAccount(), 
                          user.getKeyNumber(), 
                          Integer.toString(user.getTrips().size()),
                          Integer.toString(timeSum),
                          Integer.toString(debt)};
                debtData.addAll(Arrays.asList(userDebt));
            }
        }
        return debtData;
    }
    
    /**
     * 
     * @param instanceName
     * @return
     */
    public DStation getDStation(String instanceName) {
        return dockingStationMap.get(instanceName);
    }

}
