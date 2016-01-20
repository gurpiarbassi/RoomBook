package com.gurps.roombooking.domain;

import static com.gurps.roombooking.domain.BookingRequest.BookingRequestBuilder.aBookingRequest;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Test;

public class BookingRequestSchedulerTest {

	private final BookingRequestScheduler bookingRequestScheduler = new BookingRequestScheduler();
	
	@Test
	public void testValidMeetingsScheduled() {
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
			    .withMeetingDuration(1)
			    .withMeetingStartTime(LocalTime.of(10, 50))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(LocalDate.of(2011, 10, 25))
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, SortedSet<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.calculate(batch);
		assertEquals(1, calculatedSchedule.size());
		assertEquals(2, calculatedSchedule.get(LocalDate.of(2011, 10, 25)).size());
	}

}
