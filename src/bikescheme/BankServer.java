package bikescheme;

import java.util.*;

public class BankServer extends AbstractOutputDevice {

    public BankServer(String instanceName) {
        super(instanceName);
    }

    public void chargeUsers(Collection<User> users, List<String> data) {
        logger.fine(getInstanceName());

        String deviceClass = "BankServer";
        String deviceInstance = getInstanceName();
        String messageName = "chargeUser";

        List<String> messageArgs = new ArrayList<String>();
        String[] preludeArgs = 
            { "unordered-tuples","5",
              "UserName", "KeyID", "#Trips", "TimeSum", "DebitAmount" };

        messageArgs.addAll(Arrays.asList(preludeArgs));
        messageArgs.addAll(data);
        
        super.sendEvent(
                new Event(
                    Clock.getInstance().getDateAndTime(), 
                    deviceClass,
                    deviceInstance,
                    messageName,
                    messageArgs));
    }


}