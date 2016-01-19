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
import com.gurps.roombooking.domain.IBookingRequest;

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
        final BookingRequest bookingRequest 
            = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(8, 0),
                2,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest);
        
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingStartAfterOfficeHours() {
        final BookingRequest bookingRequest 
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
            LocalDate.of(2014, 8, 8),
            LocalTime.of(18, 0),
            2,
            "EMP01");
        
        //TODO factory for service classes or DI.
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest);
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingEndAfterOfficeHours() {
        final BookingRequest bookingRequest 
            = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 8, 8),
                LocalTime.of(16, 0),
                5,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        final Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest);
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());
    }
    
    @Test
    public void testBookingEndBeforeOfficeHours() {
        final BookingRequest bookingRequest
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 8, 8),
                LocalTime.of(14, 0),
                11,
                "EMP01");
        
        //TODO factory for service classes or DI.
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        final Set<BookingRequest> bookings = new TreeSet<>();
        bookings.add(bookingRequest);
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest);
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        assertEquals(0, output.size());        
    }
    
    @Test
    public void testOverlappingBooking(){
        final BookingRequest bookingRequest1
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(10, 0),
                5,
                "EMP01");
        
        final BookingRequest bookingRequest2
        = makeBookingRequest(LocalDate.of(2014, 3, 3), LocalTime.of(9, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(11, 0),
                2,
                "EMP02");
        
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest1);
        batch.addBookingRequest(bookingRequest2);
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        assertEquals(1, output.size());
       
        final Set<LocalDate> keys = output.keySet();
        final LocalDate meetingDate = keys.iterator().next();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        final Set<IBookingRequest> bookingRequest = output.get(meetingDate);
        assertEquals(1, bookingRequest.size());
        
        final IBookingRequest booking = bookingRequest.iterator().next();
        
        assertEquals("EMP02", booking.getEmployeeId());
        assertEquals(Integer.valueOf(2), booking.getMeetingDuration());
        assertEquals(LocalTime.of(11, 0), booking.getMeetingStartTime());
        assertEquals(LocalTime.of(13, 0), booking.getMeetingEndDateTime().toLocalTime());
        
    }
    
    @Test
    public void testConsecutiveMeetingsBoundary(){
        final BookingRequest bookingRequest1 
        = makeBookingRequest(LocalDate.of(2014, 3, 3), LocalTime.of(9, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(16, 0),
                1,
                "EMP02");
        
        
        final BookingRequest bookingRequest2        
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(12, 1, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP01");
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest1);
        batch.addBookingRequest(bookingRequest2);
        
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(1, output.size());
        final Set<Entry<LocalDate, SortedSet<IBookingRequest>>> entries = output.entrySet();
        final Entry<LocalDate, SortedSet<IBookingRequest>> entry = entries.iterator().next();
        final LocalDate meetingDate = entry.getKey();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        final SortedSet<IBookingRequest> bookingRequests = entry.getValue();
        assertEquals(2, bookingRequests.size());
        
        final IBookingRequest first = bookingRequests.first();
        final IBookingRequest second = bookingRequests.last();
        
        assertEquals("EMP01", first.getEmployeeId());
        assertEquals(Integer.valueOf(1), first.getMeetingDuration());
        assertEquals(LocalTime.of(15, 0), first.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), first.getMeetingEndDateTime().toLocalTime());
        
        assertEquals("EMP02", second.getEmployeeId());
        assertEquals(Integer.valueOf(1), second.getMeetingDuration());
        assertEquals(LocalTime.of(16, 0), second.getMeetingStartTime());
        assertEquals(LocalTime.of(17, 0), second.getMeetingEndDateTime().toLocalTime());
    }
    
    @Test
    public void testDuplicateBooking(){
        final BookingRequest bookingRequest1        
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 40, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP01");
        
        final BookingRequest bookingRequest2
        = makeBookingRequest(LocalDate.of(2014, 3, 8), LocalTime.of(13, 2, 1),
                LocalDate.of(2014, 3, 8),
                LocalTime.of(15, 0),
                1,
                "EMP02");
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 0));
        batch.addBookingRequest(bookingRequest1);
        batch.addBookingRequest(bookingRequest2);
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);
        
        assertEquals(1, output.size());
        final Set<Entry<LocalDate, SortedSet<IBookingRequest>>> entries = output.entrySet();
        final Entry<LocalDate, SortedSet<IBookingRequest>> entry = entries.iterator().next();
        final LocalDate meetingDate = entry.getKey();
        assertEquals(LocalDate.of(2014, 3, 8), meetingDate);
        final SortedSet<IBookingRequest> bookingRequests = entry.getValue();
        assertEquals(1, bookingRequests.size());
        
        final IBookingRequest bookingRequest = bookingRequests.first();
        
        assertEquals("EMP02", bookingRequest.getEmployeeId());
        assertEquals(Integer.valueOf(1), bookingRequest.getMeetingDuration());
        assertEquals(LocalTime.of(15, 0), bookingRequest.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), bookingRequest.getMeetingEndDateTime().toLocalTime());
        
    }
    
    
   /**
    * This is the sample given in the exercise
    **/
    @Test
    public void testSampleBookingBatch(){
        final BookingRequest bookingRequest1 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(10, 17, 6),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP001");
        
        final BookingRequest bookingRequest2 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(12, 34, 56),
                LocalDate.of(2011, 3, 21), LocalTime.of(9, 0), 2,"EMP002");
        
        final BookingRequest bookingRequest3 = makeBookingRequest(LocalDate.of(2011, 3, 16), LocalTime.of(9, 28, 23),
                LocalDate.of(2011, 3, 22), LocalTime.of(14, 0), 2,"EMP003");
        
        final BookingRequest bookingRequest4 = makeBookingRequest(LocalDate.of(2011, 3, 17), LocalTime.of(11, 23, 45),
                LocalDate.of(2011, 3, 22), LocalTime.of(16, 0), 1,"EMP004");
        
        final BookingRequest bookingRequest5 = makeBookingRequest(LocalDate.of(2011, 3, 15), LocalTime.of(17, 29, 12),
                LocalDate.of(2011, 3, 21), LocalTime.of(16, 0), 3,"EMP005");
        
        final ScheduleCalculatorService calcService = new BasicCalculatorService();
        
        final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(9, 0), LocalTime.of(17, 30));
        batch.addBookingRequest(bookingRequest1);
        batch.addBookingRequest(bookingRequest2);
        batch.addBookingRequest(bookingRequest3);
        batch.addBookingRequest(bookingRequest4);
        batch.addBookingRequest(bookingRequest5);
        
        
        final Map<LocalDate, SortedSet<IBookingRequest>> output = calcService.calculate(batch);        
        assertEquals(2, output.size());
        
        final SortedSet<IBookingRequest> day1Requests = output.get(LocalDate.of(2011, 3, 21));
        assertEquals(1, day1Requests.size());
        
        final IBookingRequest day1Request = day1Requests.iterator().next();
        assertEquals("EMP002", day1Request.getEmployeeId());
        assertEquals(Integer.valueOf(2), day1Request.getMeetingDuration());
        assertEquals(LocalTime.of(9, 0), day1Request.getMeetingStartTime());
        assertEquals(LocalTime.of(11, 0), day1Request.getMeetingEndDateTime().toLocalTime());
        
        final SortedSet<IBookingRequest> day2Requests = output.get(LocalDate.of(2011, 3, 22));
        assertEquals(2, day2Requests.size());
        
        final IBookingRequest day2First = day2Requests.first();
        final IBookingRequest day2Second = day2Requests.last();
        
        assertEquals("EMP003", day2First.getEmployeeId());
        assertEquals(Integer.valueOf(2), day2First.getMeetingDuration());
        assertEquals(LocalTime.of(14, 0), day2First.getMeetingStartTime());
        assertEquals(LocalTime.of(16, 0), day2First.getMeetingEndDateTime().toLocalTime());
        
        assertEquals("EMP004", day2Second.getEmployeeId());
        assertEquals(Integer.valueOf(1), day2Second.getMeetingDuration());
        assertEquals(LocalTime.of(16, 0), day2Second.getMeetingStartTime());
        assertEquals(LocalTime.of(17, 0), day2Second.getMeetingEndDateTime().toLocalTime());
    }
    
    private BookingRequest makeBookingRequest(final LocalDate requestDate, final LocalTime requestTime, final LocalDate meetingDate,
            final LocalTime meetingStartTime, final int duration, final String employeeNumber){
        return  BookingRequest.BookingRequestBuilder.aBookingRequest(requestDate, requestTime)
        .withMeetingDate(meetingDate)
        .withMeetingStartTime(meetingStartTime)
        .withMeetingDuration(duration)
        .withEmployee(employeeNumber)
        .build();
    }
    
}
