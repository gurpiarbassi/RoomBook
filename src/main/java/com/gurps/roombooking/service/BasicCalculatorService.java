package com.gurps.roombooking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.BookingRequestBatch;
import com.gurps.roombooking.domain.ScheduledMeetingComparator;

/**
 * The key business rules are defined in this class.
 * 
 * @author Gurps Bassi  gurpiar.bassi@gmail.com
 *
 */
public class BasicCalculatorService implements ScheduleCalculatorService{

    @Override
    /**
     * @param batch the BookingRequestBatch consisting of individual submissions
     * @return Map consisting of meeting date against a set of BookingRequests in chronological order
     * 
     * Any booking requests that break the business ruling will not be calculated and will be skipped
     * e.g. if a meeting clashes with another one then the one that was submitted first takes precedence.
     */
    public Map<LocalDate, SortedSet<BookingRequest>> calculate(final BookingRequestBatch batch) {
        
        System.out.println("....calculating output ....");
        
        Map<LocalDate, SortedSet<BookingRequest>> meetingsSchedule = new TreeMap<>();
        if(batch != null){
            for(BookingRequest booking : batch.getBookingRequests()){
                if(isOutsideOfficeHours(booking, batch.getOpeningTime(), batch.getClosingTime())){
                    System.out.println("Meeting occurs outside office hours. Req =  " 
                            + booking.getRequestDate() + " " + booking.getRequestTime());
                }else{
                    
                    LocalDate meetingDate = booking.getMeetingDate();
                    SortedSet<BookingRequest> meetings = meetingsSchedule.get(meetingDate);
                    if(meetings == null){
                        meetings = new TreeSet<>(new ScheduledMeetingComparator());
                        meetingsSchedule.put(meetingDate, meetings);
                    }
                    
                    if(!meetings.add(booking)){
                        System.out.println("Conflicting booking found for " + booking.getRequestDate() + " " + booking.getRequestTime());
                    }
                }
            }
        }
        
        //System.out.println("After all that fun we have : " + meetingsSchedule);
        
        return meetingsSchedule;
    }
    
    /**
     * Check to see if the booking is within office hours.
     * Bookings outside office hours cannot be placed and as a result the entire
     * submission if invalidated.
     * @param booking The Booking Request
     * @return true if the booking falls outside the company office hours. False otherwise
     */
    private boolean isOutsideOfficeHours(final BookingRequest booking, final LocalTime openingTime,
            final LocalTime closingTime){
        return booking.getMeetingStartTime().isBefore(openingTime) ||
               booking.getMeetingStartTime().isAfter(closingTime) ||
               booking.getMeetingEndDateTime().toLocalTime().isAfter(closingTime) ||
               booking.getMeetingEndDateTime().toLocalTime().isBefore(openingTime);
    }

}
