package com.gurps.roombooking.domain;

import java.util.Comparator;

/**
 * Custom comparator to produce desired output for bookings
 * 
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class ScheduledMeetingComparator implements Comparator<BookingRequest> {
    
    @Override
    
    /*
     * This implementation compares two bookings
     * @return 0 if the times overlap one another or if the bookings have equal
     * meeting date, meeting start time and meeting end time.
     * 
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final BookingRequest first, final BookingRequest second) {

        // start by comparing the meeting date
        int result = first.getMeetingDate().compareTo(second.getMeetingDate());

        if (result == 0) {
            
            if(overlaps(first, second)){
                return 0;
            }
            
            // meeting dates are the same so look at the meeting start time next
            result = first.getMeetingStartTime().compareTo(second.getMeetingStartTime());

            if (result == 0) {
                // start time is the same so look at the end time
                result = first.getMeetingEndDateTime().toLocalTime()
                        .compareTo(second.getMeetingEndDateTime().toLocalTime());
            }
        }
        return result;
    }

    /**
     * Compute if the meetings clash i.e. the times overlap in the same day
     * @param first The first booking request
     * @param second The second booking request
     * @return true if the meetings clash, false otherwise.
     */
    private boolean overlaps(final BookingRequest first, final BookingRequest second) {
        
        boolean clash = first.getMeetingStartTime().isBefore(second.getMeetingEndDateTime().toLocalTime())
        && second.getMeetingStartTime().isBefore(first.getMeetingEndDateTime().toLocalTime());
        
        return clash;
    }
}
