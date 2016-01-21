package com.gurps.roombooking.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The key business rules are defined in this class.
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class BookingRequestScheduler implements IBookingRequestScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BookingRequestScheduler.class);
	
	
	/**
	 * @param batch the BookingRequestBatch consisting of individual submissions
	 * @return Map consisting of meeting date against a set of BookingRequests in chronological order
	 * 
	 * Any booking requests that break the business ruling will not be calculated and will be skipped
	 * e.g. if a meeting clashes with another one then the one that was submitted first takes precedence.
	 */
	@Override
	public Map<LocalDate, SortedSet<IBookingRequest>> schedule(final IBookingRequestBatch batch) {

		LOGGER.info("....calculating output ....");

		final Map<LocalDate, SortedSet<IBookingRequest>> meetingsSchedule = new TreeMap<>();
		if (batch != null) {
			for (final IBookingRequest booking : batch.getBookingRequests()) {
				if (isOutsideOfficeHours(booking, batch.getOpeningTime(), batch.getClosingTime())) {
					LOGGER.error("Meeting occurs outside office hours. Req =  {} {}",  booking.getRequestDate(), booking.getRequestTime());
				} else {

					final LocalDate meetingDate = booking.getMeetingDate();
					
					final boolean meetingAdded = meetingsSchedule.computeIfAbsent(meetingDate, m -> new TreeSet<IBookingRequest>(new ScheduledMeetingComparator())).add(booking);
									
					
//					SortedSet<IBookingRequest> meetings = meetingsSchedule.get(meetingDate);
//					if (meetings == null) {
//						meetings = new TreeSet<IBookingRequest>(new ScheduledMeetingComparator());
//						meetingsSchedule.put(meetingDate, meetings);
//					}

					if (!meetingAdded) {
						LOGGER.warn("Conflicting booking found for {} {}", booking.getRequestDate(), booking.getRequestTime());
					}
				}
			}
		}

		// System.out.println("After all that fun we have : " +
		// meetingsSchedule);

		return meetingsSchedule;
	}

	/**
	 * Check to see if the booking is within office hours. Bookings outside
	 * office hours cannot be placed and as a result the entire submission if
	 * invalidated.
	 * 
	 * @param booking
	 *            The Booking Request
	 * @return true if the booking falls outside the company office hours. False
	 *         otherwise
	 */
	private boolean isOutsideOfficeHours(final IBookingRequest booking, final LocalTime openingTime, final LocalTime closingTime) {
		return booking.getMeetingStartTime().isBefore(openingTime) || booking.getMeetingStartTime().isAfter(closingTime)
				|| booking.getMeetingEndDateTime().toLocalTime().isAfter(closingTime) || booking.getMeetingEndDateTime().toLocalTime().isBefore(openingTime);
	}

}
