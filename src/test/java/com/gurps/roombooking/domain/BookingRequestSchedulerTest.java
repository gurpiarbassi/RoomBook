package com.gurps.roombooking.domain;

import static com.gurps.roombooking.domain.BookingRequest.BookingRequestBuilder.aBookingRequest;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class BookingRequestSchedulerTest {

	private final BookingRequestScheduler bookingRequestScheduler = new BookingRequestScheduler();
	
	@Test
	public void testTwoBookingsSameDay() {
		final LocalDate meetingDate = LocalDate.of(2011, 10, 25);
		
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
			    .withMeetingDuration(1)
			    .withMeetingStartTime(LocalTime.of(10, 50))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(1, calculatedSchedule.size());
		assertEquals(2, calculatedSchedule.get(meetingDate).size());
	}
	
	@Test
	public void testTwoBookingsDifferentDay() {
		final LocalDate meetingDate1 = LocalDate.of(2011, 10, 25);
		final LocalDate meetingDate2 = LocalDate.of(2011, 10, 26);
		
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(meetingDate1)
			    .withMeetingDuration(1)
			    .withMeetingStartTime(LocalTime.of(10, 50))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 13), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(meetingDate2)
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(2, calculatedSchedule.size());
		assertEquals(1, calculatedSchedule.get(meetingDate1).size());
		assertEquals(1, calculatedSchedule.get(meetingDate2).size());
	}
	
	@Test
	public void testEmptyBatch(){
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(0, calculatedSchedule.size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullBatch(){
		final BookingRequestBatch batch = null;
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(0, calculatedSchedule.size());
	}
	
	@Test
	public void ignoresMeetingStartingBeforeOfficeOpening(){
		final LocalDate meetingDate = LocalDate.of(2011, 10, 25);
		
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
			    .withMeetingDuration(1)
			    .withMeetingStartTime(LocalTime.of(9, 50))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(1, calculatedSchedule.size());
		assertEquals(1, calculatedSchedule.get(meetingDate).size());
	}
	
	@Test
	public void ignoresMeetingStartingAfterClosing(){
		final LocalDate meetingDate = LocalDate.of(2011, 10, 25);
		
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
			    .withMeetingDuration(1)
			    .withMeetingStartTime(LocalTime.of(17, 1))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(1, calculatedSchedule.size());
		assertEquals(1, calculatedSchedule.get(meetingDate).size());
	}
	
	@Test
	public void ignoresMeetingEndTimeAfterClosing(){
		final LocalDate meetingDate = LocalDate.of(2011, 10, 25);
		
		final BookingRequest bookingRequest1 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 33))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
			    .withMeetingDuration(5)
			    .withMeetingStartTime(LocalTime.of(16, 1))
			    .build();

		final BookingRequest bookingRequest2 = aBookingRequest(LocalDate.of(2011, 10, 12), LocalTime.of(1, 34))
				.withEmployee("abc")
				.withMeetingDate(meetingDate)
				.withMeetingDuration(1)
				.withMeetingStartTime(LocalTime.of(13, 50))
				.build();
		
		final BookingRequestBatch batch = new BookingRequestBatch(LocalTime.of(10, 0), LocalTime.of(17, 0));
		batch.addBookingRequest(bookingRequest1);
		batch.addBookingRequest(bookingRequest2);
		final Map<LocalDate, List<IBookingRequest>> calculatedSchedule = bookingRequestScheduler.schedule(batch);
		assertEquals(1, calculatedSchedule.size());
		assertEquals(1, calculatedSchedule.get(meetingDate).size());
	}
}
