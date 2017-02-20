/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *  
 * Docking Station.
 * 
 * @author pbj
 *
 */
public class DStation implements StartRegObserver, ViewActivityObserver, FindFreePointsObserver, DStationInterface{
    public static final Logger logger = Logger.getLogger("bikescheme");

    private String instanceName;
    private int eastPos;
    private int northPos;
    
    private DSTouchScreen touchScreen;
    private CardReader cardReader; 
    private KeyIssuer keyIssuer;
    private List<DPoint> dockingPoints;
    private KeyReader keyReader;
    
    private HubInterface hub;
    /**
     * 
     * Construct a Docking Station object with touch screen, card reader
     * and key issuer interface devices and a connection to a number of
     * docking points.
     * 
     * If the instance name is <foo>, then the Docking Points are named
     * <foo>.1 ... <foo>.<numPoints> . 
     * 
     * @param instanceName
     */
    public DStation(
            String instanceName,
            int eastPos,
            int northPos,
            int numPoints) {
        
     // Construct and make connections with interface devices
        
        this.instanceName = instanceName;
        this.eastPos = eastPos;
        this.northPos = northPos;
        
        touchScreen = new DSTouchScreen(instanceName + ".ts");
        touchScreen.setObserver(this);
        // Set observers to recognise "view activity" and "find free points" options
        touchScreen.setViewActivityObserver(this);
        touchScreen.setFindFreePointsObserver(this);
        
        cardReader = new CardReader(instanceName + ".cr");
        
        keyIssuer = new KeyIssuer(instanceName + ".ki");
        
        keyReader = new KeyReader(instanceName + ".kr");
        
        dockingPoints = new ArrayList<DPoint>();
        
        for (int i = 1; i <= numPoints; i++) {
            DPoint dp = new DPoint(instanceName + "." + i, i - 1);
            dockingPoints.add(dp);
        }
    }
       
    void setDistributor(EventDistributor d) {
        touchScreen.addDistributorLinks(d); 
        cardReader.addDistributorLinks(d);
        keyReader.addDistributorLinks(d);
        for (DPoint dp : dockingPoints) {
            dp.setDistributor(d);
        }
    }
    
    void setCollector(EventCollector c) {
        touchScreen.setCollector(c);
        cardReader.setCollector(c);
        keyIssuer.setCollector(c);
        for (DPoint dp : dockingPoints) {
            dp.setCollector(c);
        }
    }
    
    /** 
     * Implementation of docking station functionality for 
     * "register user" use case.
     * 
     * Method called on docking station receiving a "start registration"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */
    public void startRegReceived(String personalInfo) {
        logger.fine("Starting on instance " + getInstanceName());
        cardReader.requestCard();  // Generate output event
        logger.fine("At position 1 on instance " + getInstanceName());
        
        String card = cardReader.checkCard();    // Pull in non-triggering input event
        logger.fine("At position 2 on instance " + getInstanceName());
        
        String keyID = keyIssuer.issueKey(); // Generate output event
        
        // Create a new User class in the Hub system and links it to the key.
        hub.newUser(personalInfo, keyID, card);
    }
    
    /**
     * Link this station and the DPoints in the station to the parent hub.
     *
     */
    public void setHub(HubInterface h){
        this.hub = h;
        for (DPoint dp : dockingPoints) {
            dp.setInterface(this, hub);
        }
    }
    
    /**
     * Implementation of docking station functionality for 
     * "view activity" use case.
     * 
     * Method called on docking station receiving a "view activity"
     * triggering input event at the touch screen.
     * 
     */
    @Override
    public void viewActivityReceived(){
        logger.fine(getInstanceName());
        
        touchScreen.showPrompt("PLEASE INSERT YOUR KEY.");
        String keyID = keyReader.waitForKeyInsertion();
        
        ArrayList<String> userInfoData;
        
        userInfoData = hub.userActivity(keyID);
        
        touchScreen.showUserActivity(userInfoData);;
        
    }
    
    /**
     * Implementation of docking station functionality for 
     * "find free points" use case.
     * 
     * Method called on docking station receiving a "find free points"
     * triggering input event at the touch screen.
     * 
     * It asks the hub to return occupancy lists, and then asks the
     * touch screen display to show the occupancy data.
     * 
     */
    @Override
    public void findFreePointsReceived(){
        logger.fine(getInstanceName());
        
        ArrayList<String> occupancyList;
        
        occupancyList = hub.findFreePoints(this);
        
        touchScreen.showFreePoints(occupancyList);
        
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public int getEastPos() {
        return eastPos;
    }
    
    public int getNorthPos() {
        return northPos;
    }
    
    /**
     * Return the number of DPoints
     * 
     * @return
     */
    public int getNoDPoints(){
        return dockingPoints.size();
    }
    
    /**
     * Return number of occupied DPoints Modified
     * 
     * @return
     */
    public int getOccupiedDPoints(){
        int occupiedNumber = 0;
        for (DPoint p : dockingPoints){
            if (p.isOccupied()) occupiedNumber++;
        }
        
        return occupiedNumber;
    }
    
 

}
