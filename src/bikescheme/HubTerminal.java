/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model of a terminal with a keyboard, mouse and monitor.
 * 
 * @author pbj
 *
 */
public class HubTerminal extends AbstractIODevice {

    /**
     * 
     * @param instanceName  
     */
    public HubTerminal(String instanceName) {
        super(instanceName);   
    }
    
    // Fields and methods for device input function
    
    private AddDStationObserver observer;
    
    public void setObserver(AddDStationObserver o) {
        observer = o;
    }
    
    private ShowStatsObserver showStatsObserver;
    
    public void setShowStatsObserver(ShowStatsObserver o) {
        showStatsObserver = o;
    }

    
    /** 
     *    Select device action based on input event message
     *    
     *    @param e
     */
    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("addDStation") 
                && e.getMessageArgs().size() == 4) {
            
            String instanceName = e.getMessageArgs().get(0);
            int eastPos = Integer.parseInt(e.getMessageArg(1));
            int northPos =  Integer.parseInt(e.getMessageArg(2));
            int numPoints =  Integer.parseInt(e.getMessageArg(3));
            
            addDStation(instanceName, eastPos, northPos, numPoints);
            
        } else if (e.getMessageName().equals("viewStats") 
                && e.getMessageArgs().size() == 0){
            viewStats();
        } else {
            super.receiveEvent(e);
        } 
    }
    
    /**
     * Handle request to add a new docking station
     * 
     * @param instanceName
     * @param eastPos
     * @param northPos
     * @param numPoints
     */
    public void addDStation(
            String instanceName, 
            int eastPos, 
            int northPos,
            int numPoints) {
        logger.fine(getInstanceName());
        
        
        observer.addDStation(instanceName, eastPos, northPos, numPoints);
    }
    
    /**
     * Generates stats for the operator
     * 
     */
    public void viewStats(){
        logger.fine(getInstanceName());
        
        showStatsObserver.showStatsReceived();
    }
    
    public void showStats(List<String> stats) {
        logger.fine(getInstanceName());
        
        String deviceClass = "HubTerminal";
        String deviceInstance = getInstanceName();
        String messageName = "showStats";
        
        List<String> messageArgs = new ArrayList<String>();
        String[] preludeArgs = 
            {"ordered-tuples","2"};
        messageArgs.addAll(Arrays.asList(preludeArgs));
        messageArgs.addAll(stats);
        
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                messageArgs));
       
    }
   
}
