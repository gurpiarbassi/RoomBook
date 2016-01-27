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
	
}
