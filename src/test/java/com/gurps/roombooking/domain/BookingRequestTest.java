package com.gurps.roombooking.domain;

import static com.gurps.roombooking.domain.BookingRequest.BookingRequestBuilder.aBookingRequest;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

public class BookingRequestTest {
	
	@Test
	public void canCreate(){
		final LocalDate requestDate = LocalDate.of(2011, 10, 12);
		final LocalTime requestTime = LocalTime.of(14, 33);
		final LocalDate meetingDate = LocalDate.of(2011, 10, 25);
		final LocalTime meetingStartTime = LocalTime.of(13, 50);
		final Integer duration = 10;
		final String employee = "abc";
		final BookingRequest bookingRequest = aBookingRequest(requestDate, requestTime).withEmployee(employee)
																					   .withMeetingDate(meetingDate)
																					   .withMeetingDuration(duration)
																					   .withMeetingStartTime(meetingStartTime)
																					   .build();
		
		assertEquals(requestDate, bookingRequest.getRequestDate());
		assertEquals(requestTime, bookingRequest.getRequestTime());
		assertEquals(meetingDate, bookingRequest.getMeetingDate());
		assertEquals(meetingStartTime, bookingRequest.getMeetingStartTime());
		assertEquals(duration, bookingRequest.getMeetingDuration());
		assertEquals(employee, bookingRequest.getEmployeeId());
		
		final LocalDateTime start = LocalDateTime.of(meetingDate, meetingStartTime);
		assertEquals(start.plus(duration, HOURS), bookingRequest.getMeetingEndDateTime());
		
	}
	
	@Test
	public void testComparable_bothSame(){
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 33))
												.withEmployee("abc")
												.withMeetingDate(LocalDate.of(2011, 10, 25))
											    .withMeetingDuration(10)
											    .withMeetingStartTime(LocalTime.of(13, 50))
											    .build();
		
		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 33))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
			    .withMeetingDuration(10)
			    .withMeetingStartTime(LocalTime.of(13, 50))
			    .build();
		
		assertEquals(0, bookingRequest1.compareTo(bookingRequest2));
		assertEquals(0, bookingRequest2.compareTo(bookingRequest1));
	}
	
	@Test
	public void testComparable_DifferentRequestTime(){
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 33))
												.withEmployee("abc")
												.withMeetingDate(LocalDate.of(2011, 10, 25))
											    .withMeetingDuration(10)
											    .withMeetingStartTime(LocalTime.of(13, 50))
											    .build();
		
		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 34))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
			    .withMeetingDuration(10)
			    .withMeetingStartTime(LocalTime.of(13, 50))
			    .build();
		
		assertEquals(-1, bookingRequest1.compareTo(bookingRequest2));
		assertEquals(1, bookingRequest2.compareTo(bookingRequest1));
		
	}
	
	@Test
	public void testComparable_DifferentRequestDates(){
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 33))
												.withEmployee("abc")
												.withMeetingDate(LocalDate.of(2011, 10, 25))
											    .withMeetingDuration(10)
											    .withMeetingStartTime(LocalTime.of(13, 50))
											    .build();
		
		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 13), LocalTime.of(14, 33))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
			    .withMeetingDuration(10)
			    .withMeetingStartTime(LocalTime.of(13, 50))
			    .build();
		
		assertEquals(-1, bookingRequest1.compareTo(bookingRequest2));
		assertEquals(1, bookingRequest2.compareTo(bookingRequest1));
		
	}
	
	@Test
	public void testComparable_DifferentDateAndTime(){
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(14, 33))
												.withEmployee("abc")
												.withMeetingDate(LocalDate.of(2011, 10, 25))
											    .withMeetingDuration(10)
											    .withMeetingStartTime(LocalTime.of(13, 50))
											    .build();
		
		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 13), LocalTime.of(14, 34))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
			    .withMeetingDuration(10)
			    .withMeetingStartTime(LocalTime.of(13, 50))
			    .build();
		
		assertEquals(-1, bookingRequest1.compareTo(bookingRequest2));
		assertEquals(1, bookingRequest2.compareTo(bookingRequest1));
		
	}

}
