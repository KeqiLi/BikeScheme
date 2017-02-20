package bikescheme;


import java.util.Calendar;
import java.util.Date;

public class Trip {
    private String startStation, endStation;
    private Date start, end;
    private int day;
    private int length;

    public Trip(String station){
        this.startStation = station;
        this.day = Clock.getInstance().getDateAndTimeAsCalendar().get(Calendar.DAY_OF_MONTH);
        this.start = Clock.getInstance().getDateAndTime();
    }
    
    public void endTrip(String station){
        this.endStation = station;
        this.end = Clock.getInstance().getDateAndTime();
        this.length = Clock.minutesBetween(start, end);
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }
    
    public Date getStart() {
        return start;
    }

    public int getDay() {
        return day;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    
}
