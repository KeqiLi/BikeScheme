package bikescheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

public class EventTest {

    @Test
    public void testEqual() {
        assertEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, d") );
    }
    @Test
    public void testParam1() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, c, e") );
    }
    
    @Test
    public void testParam2() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, b, e, d") );
    }
    
    @Test
    public void testParam3() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, a, e, c, d") );
    }
    
    @Test
    public void testParam4() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,m, e, b, c, d") );
    }
    
    @Test
    public void testParam5() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,i,e, a, b, c, d") );
    }
    
    @Test
    public void testParam6() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,C,e,m, a, b, c, d") );
    }
    
    @Test
    public void testParam7() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:00,A,i,m, a, b, c, d") );
    }
    
    @Test
    public void testParam8() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("1 00:01,C,i,m, a, b, c, d") );
    }
    
    @Test
    public void testParam9() {
        assertNotEquals(
                new Event("1 00:00,C,i,m, a, b, c, d"),
                new Event("2 00:00,C,i,m, a, b, c, d") );
    }
    
    /**
     * This tests two lists of events with different sequences of same events.
     * The dates are the same.
     * 
     */
    @Test
    public void testListSameDay() {
        ArrayList<Event> events1 = new ArrayList<Event>();
        ArrayList<Event> events2 = new ArrayList<Event>();
        
        events1.add(new Event("1 00:00,C,i,m, a, b, c, d"));
        events1.add(new Event("1 00:00,A,o,m, a, b, c, d"));
        
        events2.add(new Event("1 00:00,A,o,m, a, b, c, d"));
        events2.add(new Event("1 00:00,C,i,m, a, b, c, d"));
        
        if (Event.listEqual(events1, events2) == false) {
            fail("Should have returned true.");
        }
    }

    /**
     * This tests two lists of events with different sequences of same events.
     * But the dates are not the same.
     * 
     */
    @Test
    public void testListNotSameDay() {
        ArrayList<Event> events1 = new ArrayList<Event>();
        ArrayList<Event> events2 = new ArrayList<Event>();
        
        events1.add(new Event("1 00:00,C,i,m, a, b, c, d"));
        events1.add(new Event("2 18:00,A,o,m, a, b, c, d"));
        
        events2.add(new Event("2 18:00,A,o,m, a, b, c, d"));
        events2.add(new Event("1 00:00,C,i,m, a, b, c, d"));
        
        if (Event.listEqual(events1, events2) == true) {
            fail("Should have returned false.");
        }
    }
}
