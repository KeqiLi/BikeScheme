package bikescheme;

import java.util.ArrayList;

public interface HubInterface {

    public void bikeTakeOut(String keyID, String bikeID, DPoint d);
    
    public void bikeDocked(String bikeID, DPoint d);
    
    public void newUser(String info, String keyID, String card);
    
    public ArrayList<String> userActivity(String keyID);
    
    public boolean containsBike(String bikeID);
    
    public ArrayList<String> findFreePoints(DStation s);
    
    public void fault(DPoint dp);
   
    
}
