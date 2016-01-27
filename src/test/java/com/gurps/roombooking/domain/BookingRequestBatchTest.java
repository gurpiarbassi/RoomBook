package com.gurps.roombooking.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.Test;

public class BookingRequestBatchTest {

	@Test
	public void canCreate() {
		final LocalTime officeOpeningTime = LocalTime.of(9, 55);
		final LocalTime officeClosingTime = LocalTime.of(17, 55);
		final BookingRequestBatch batch = new BookingRequestBatch(officeOpeningTime, officeClosingTime);
		assertSame(officeOpeningTime, batch.getOpeningTime());
		assertSame(officeClosingTime, batch.getClosingTime());
		assertNotNull(batch.getBookingRequests());
	}
	
	@Test
	public void canAddBooking() {
		final LocalTime officeOpeningTime = LocalTime.of(9, 55);
		final LocalTime officeClosingTime = LocalTime.of(17, 55);
		final BookingRequestBatch batch = new BookingRequestBatch(officeOpeningTime, officeClosingTime);
		
		final BookingRequest bookingRequest = BookingRequest.BookingRequestBuilder.aBookingRequest(LocalDate.of(2011, 3, 8), LocalTime.of(9, 30, 0))
																								.withMeetingDate(LocalDate.of(2011, 03, 11))
																								.withEmployee("abc")
																								.withMeetingStartTime(LocalTime.of(11, 0))
																								.withMeetingDuration(2)
																								.build();
		batch.addBookingRequest(bookingRequest);
		
		final List<IBookingRequest> bookingRequests = batch.getBookingRequests();
		assertEquals(1, bookingRequests.size());
		assertSame(bookingRequest, bookingRequests.iterator().next());
	}
	
}
