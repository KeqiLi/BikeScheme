/**
 * 
 */
package bikescheme;

import java.util.logging.Logger;
import java.util.Date;

/**
 * 
 * Docking Point for a Docking Station.
 * 
 * @author pbj
 *
 */
public class DPoint implements KeyInsertionObserver, BikeDockingObserver, FaultButtonPressedObserver {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private KeyReader keyReader;
    private OKLight okLight;
    private BikeSensor bikeSensor;
    private BikeLock bikeLock;
    private String instanceName;
    private int index;
    private boolean occupiedStatus;             // This status indicates if it is occupied;
    private String bikeID;
    private FaultButton faultButton;
    private Date lastDocked;
    private FaultLight faultLight;
    
    private HubInterface hub;
    private DStationInterface station;


    /**
     * 
     * Construct a Docking Point object with a key reader and green ok light
     * interface devices.
     * 
     * @param instanceName
     *            a globally unique name
     * @param index
     *            of reference to this docking point in owning DStation's list
     *            of its docking points.
     */

    public DPoint(String instanceName, int index) {

        // Construct and make connections with interface devices

        keyReader = new KeyReader(instanceName + ".kr");
        keyReader.setObserver(this);
        okLight = new OKLight(instanceName + ".ok");
        // NEW PARAMETERS
        bikeSensor = new BikeSensor(instanceName + ".bs");
        bikeSensor.setObserver(this);
        bikeLock = new BikeLock(instanceName + ".bl");
        occupiedStatus = false;
        faultButton = new FaultButton(instanceName + ".fb");
        faultButton.setObserver(this);
        faultLight = new FaultLight(instanceName + ".fl");

        this.instanceName = instanceName;
        this.index = index;
    }

    public void setDistributor(EventDistributor d) {
        bikeSensor.addDistributorLinks(d);
        keyReader.addDistributorLinks(d);
        faultButton.addDistributorLinks(d);
    }

    public void setCollector(EventCollector c) {
        bikeLock.setCollector(c);
        okLight.setCollector(c);
        faultLight.setCollector(c);
    }

    public String getInstanceName() {
        return instanceName;
    }

    public int getIndex() {
        return index;
    }

    public void setInterface(DStationInterface s, HubInterface h) {
        this.station = s;
        this.hub = h;
    }
    
    /**
     * Implementation of docking point functionality on key insertion.
     * 
     * Key Insertion triggers unlocking the bike and flashes the light.
     */
    public void keyInserted(String keyId) {
        logger.fine(getInstanceName());
        bikeLock.unlock();
        okLight.flash();
        hub.bikeTakeOut(keyId, bikeID, this);
        this.occupiedStatus = false;

    }

    /**
     * A bike is docked.
     * 
     * If the hub contains this bike in system, run "return bike".
     * 
     */
    public void bikeDocked(String bikeID) {
        logger.fine(getInstanceName());
  
        okLight.flash();
        bikeLock.lock();
        hub.bikeDocked(bikeID, this);
        this.bikeID = bikeID;
        this.occupiedStatus = true;
        this.lastDocked = Clock.getInstance().getDateAndTime();
    }

    
    /**
     * A fault button is pressed, if it's pressed within 2 mins,
     * report FAULT.
     * 
     */
    public void faultButtonPressed(){
        logger.fine(getInstanceName());
        if (Clock.minutesBetween(lastDocked, Clock.getInstance().getDateAndTime())<=2) {
            faultLight.flash();
            hub.fault(this);
        }
    }
    
    /**
     * If occupied return True else return False.
     * 
     */
    public boolean isOccupied() {
        return this.occupiedStatus;
    }
    
    public String getStationName() {
        return station.getInstanceName();
    }
    
    public String getBikeID() {
        return bikeID;
    }

}
