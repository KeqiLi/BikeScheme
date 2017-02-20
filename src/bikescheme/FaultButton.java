package bikescheme;

public class FaultButton extends AbstractInputDevice{

    private FaultButtonPressedObserver observer;
    
    public FaultButton(String instanceName) {
        super(instanceName);
    }
    
    public void setObserver(FaultButtonPressedObserver o) {
        observer = o;
    }

    @Override
    public void receiveEvent(Event e) {
        
        if (e.getMessageName().equals("pressed") 
                && e.getMessageArgs().size() == 0) {
            
            faultBike();
            
        } else {
            super.receiveEvent(e);
        }
    }
    
    /**
     * This function models the fault button being pressed.
     * 
     */
    public void faultBike(){
        logger.fine(getInstanceName());
        observer.faultButtonPressed();
    }

}
