package com.gurps.roombooking.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.BookingRequestBatch;
import com.gurps.roombooking.service.BasicCalculatorService;
import com.gurps.roombooking.service.ScheduleCalculatorService;

/**
 * 
 * The Tests operate on the ScheduleCalculatorService since that is 
 * the location of the business ruling.
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class TestBasicCalculatorService {

    @Test
    public void testBookingStartBeforeOfficeHours() {
        BookingRequest bookingRequest 
            = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(8, 0),
                2,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingStartAfterOfficeHours() {
        BookingRequest bookingRequest 
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
            LocalDate.of(2014, 8, 8),
            LocalTime.of(18, 0),
            2,
            "EMP01");
        
        //TODO factory for service classes or DI.
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingEndAfterOfficeHours() {
        BookingRequest bookingRequest 
            = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 8, 8),
                LocalTime.of(16, 0),
                5,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingEndBeforeOfficeHours() {
        BookingRequest bookingRequest
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 8, 8),
                LocalTime.of(14, 0),
                11,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());        
    }
    
    @Test
    public void testOverlappingBooking(){
        BookingRequest bookingRequest1
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(10, 0),
                5,
                "EMP01");
        
        BookingRequest bookingRequest2
        = makeBookingRequest(LocalDate.of(2014, 3, 3), LocalTime.of(9, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(11, 0),
                2,
                "EMP02");
        
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest1);
        bookings.add(bookingRequest2);
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        assertEquals(1, output.size());
       
        Set<LocalDate> keys = output.keySet();
        LocalDate meetingDate = keys.iterator().next();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        Set<BookingRequest> bookingRequest = output.get(meetingDate);
        assertEquals(1, bookingRequest.size());
        
        BookingRequest booking = bookingRequest.iterator().next();
        
        assertEquals("EMP02", booking.getEmployeeId());
        assertEquals(2, booking.getMeetingDuration());
        assertEquals(LocalTime.of(11, 0), booking.getMeetingStartTime());
        assertEquals(LocalTime.of(13, 0), booking.getMeetingEndDateTime().toLocalTime());
        
    }
    
    @Test
    public void testConsecutiveMeetingsBoundary(){
        BookingRequest bookingRequest1 
        = makeBookingRequest(LocalDate.of(2014, 3, 3), LocalTime.of(9, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(16, 0),
                1,
                "EMP02");
        
        
        BookingRequest bookingRequest2        
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(12, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP01");
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest1);
        bookings.add(bookingRequest2);
        
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(1, output.size());
        Set<Entry<LocalDate, SortedSet<BookingRequest>>> entries = output.entrySet();
        Entry<LocalDate, SortedSet<BookingRequest>> entry = entries.iterator().next();
        LocalDate meetingDate = entry.getKey();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        SortedSet<BookingRequest> bookingRequests = entry.getValue();
        assertEquals(2, bookingRequests.size());
        
        BookingRequest first = bookingRequests.first();
        BookingRequest second = bookingRequests.last();
        
        assertEquals("EMP01", first.getEmployeeId());
        assertEquals(1, first.getMeetingDuration());
        assertEquals(LocalTime.of(15, 0), first.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), first.getMeetingEndDateTime().toLocalTime());
        
        assertEquals("EMP02", second.getEmployeeId());
        assertEquals(1, second.getMeetingDuration());
        assertEquals(LocalTime.of(16, 0), second.getMeetingStartTime());
        assertEquals(LocalTime.of(17, 0), second.getMeetingEndDateTime().toLocalTime());
    }
    
    @Test
    public void testDuplicateBooking(){
        BookingRequest bookingRequest1        
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 40, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP01");
        
        BookingRequest bookingRequest2
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 2, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP02");
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest1);
        bookings.add(bookingRequest2);
        
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.setBookingRequests(bookings);
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(1, output.size());
        Set<Entry<LocalDate, SortedSet<BookingRequest>>> entries = output.entrySet();
        Entry<LocalDate, SortedSet<BookingRequest>> entry = entries.iterator().next();
        LocalDate meetingDate = entry.getKey();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        SortedSet<BookingRequest> bookingRequests = entry.getValue();
        assertEquals(1, bookingRequests.size());
        
        BookingRequest bookingRequest = bookingRequests.first();
        
        assertEquals("EMP02", bookingRequest.getEmployeeId());
        assertEquals(1, bookingRequest.getMeetingDuration());
        assertEquals(LocalTime.of(15, 0), bookingRequest.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), bookingRequest.getMeetingEndDateTime().toLocalTime());
        
    }
    
    
   /**
    * This is the sample given in the exercise
    **/
    @Test
    public void testSampleBookingBatch(){
        BookingRequest bookingRequest1 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(10, 17, 6),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP001");
        
        BookingRequest bookingRequest2 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(12, 34, 56),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP002");
        
        BookingRequest bookingRequest3 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(9, 28, 23),
                LocalDate.of(2011, 3, 22), LocalTime.of(14, 0), 2,"EMP003");
        
        BookingRequest bookingRequest4 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(11, 23, 45),
                LocalDate.of(2011, 3, 22), LocalTime.of(16, 0), 1,"EMP004");
        
        BookingRequest bookingRequest5 = makeBookingRequest(LocalDate.of(2011, 3, 15), LocalTime.of(17, 29, 12),
                LocalDate.of(2011, 3, 21), LocalTime.of(16, 0), 3,"EMP005");
        
        ScheduleCalculatorService calcService = new BasicCalculatorService();
        Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest1);
        bookings.add(bookingRequest2);
        bookings.add(bookingRequest3);
        bookings.add(bookingRequest4);
        bookings.add(bookingRequest5);
        
        BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 30));
        batch.setBookingRequests(bookings);
        
        Map<LocalDate, SortedSet<BookingRequest>> output = calcService.calculate(batch);        
        assertEquals(2, output.size());
        
        SortedSet<BookingRequest> day1Requests = output.get(LocalDate.of(2011, 3, 21));
        assertEquals(1, day1Requests.size());
        
        BookingRequest day1Request = day1Requests.iterator().next();
        assertEquals("EMP002", day1Request.getEmployeeId());
        assertEquals(2, day1Request.getMeetingDuration());
        assertEquals(LocalTime.of(9, 0), day1Request.getMeetingStartTime());
        assertEquals(LocalTime.of(11, 0), day1Request.getMeetingEndDateTime().toLocalTime());
        
        SortedSet<BookingRequest> day2Requests = output.get(LocalDate.of(2011, 3, 22));
        assertEquals(2, day2Requests.size());
        
        BookingRequest day2First = day2Requests.first();
        BookingRequest day2Second = day2Requests.last();
        
        assertEquals("EMP003", day2First.getEmployeeId());
        assertEquals(2, day2First.getMeetingDuration());
        assertEquals(LocalTime.of(14, 0), day2First.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), day2First.getMeetingEndDateTime().toLocalTime());
        
        assertEquals("EMP004", day2Second.getEmployeeId());
        assertEquals(1, day2Second.getMeetingDuration());
        assertEquals(LocalTime.of(16, 0), day2Second.getMeetingStartTime());
        assertEquals(LocalTime.of(17, 0), day2Second.getMeetingEndDateTime().toLocalTime());
    }
    
    private BookingRequest makeBookingRequest(final LocalDate requestDate, final LocalTime requestTime, final LocalDate meetingDate,
            final LocalTime meetingStartTime, final int duration, final String employeeNumber){
        return new BookingRequest.BookingRequestBuilder(requestDate, requestTime)
        .meetingDate(meetingDate)
        .meetingStart(meetingStartTime)
        .duration(duration)
        .employee(employeeNumber)
        .build();
    }
    
}
