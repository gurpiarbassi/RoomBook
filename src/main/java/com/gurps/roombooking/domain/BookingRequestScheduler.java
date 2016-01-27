package com.gurps.roombooking.domain;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class BookingRequestScheduler implements IBookingRequestScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BookingRequestScheduler.class);
	private static final Comparator<IBookingRequest> BY_SUBMISSION_DATE_TIME = new ScheduledMeetingComparator();
	
	
	/**
	 * @param batch the BookingRequestBatch consisting of individual submissions
	 * @return Map consisting of meeting date against a set of BookingRequests in chronological order
	 * 
	 * Any booking requests that break the business ruling will not be calculated and will be skipped
	 * e.g. if a meeting clashes with another one then the one that was submitted first takes precedence.
	 */
	@Override
	public Map<LocalDate, List<IBookingRequest>> schedule(final IBookingRequestBatch batch) {
		LOGGER.info("....calculating output ....");
		if(batch == null){
			throw new IllegalArgumentException("Booking request batch must be supplied");
		}
		//TODO return custom object encapsulating the map.
		
		List<IBookingRequest> bookings = batch.getBookingRequests();
		
		
		
		bookings = bookings.stream().filter(booking -> !isOutsideOfficeHours(booking, batch.getOpeningTime(), batch.getClosingTime()))
					       .sorted(BY_SUBMISSION_DATE_TIME)
					       .collect(toList());
		
		
		final List<IBookingRequest> cleansedData = removeOverlaps(bookings);
		final Map<LocalDate, List<IBookingRequest>> finalBookings = cleansedData.stream().collect(groupingBy(booking -> booking.getMeetingDate(), LinkedHashMap::new, toList()));
		
		return finalBookings;
	}
	
	private List<IBookingRequest> removeOverlaps(final List<IBookingRequest> bookings){
		final List<IBookingRequest> cleansedData = new ArrayList<>();
		IBookingRequest previousBooking = null;
		for(final IBookingRequest currentBooking : bookings){
			if(previousBooking == null || !requestsOverlap(previousBooking, currentBooking)){
				cleansedData.add(currentBooking);
				previousBooking = currentBooking;
			}
		}
		
		return cleansedData;
	}
	
	private boolean requestsOverlap(final IBookingRequest first, final IBookingRequest second) {

		final boolean sameDay = first.getMeetingDate().equals(second.getMeetingDate());
		boolean overlaps = false;
		if (sameDay) {
			overlaps = (first.getMeetingStartTime().isBefore(second.getMeetingEndDateTime().toLocalTime()) 
					&& first.getMeetingEndDateTime().toLocalTime().isAfter(second.getMeetingStartTime())) ||
					 (first.getMeetingStartTime().equals(second.getMeetingStartTime()) 
								&& first.getMeetingEndDateTime().equals(second.getMeetingEndDateTime()));
					
		}
		return overlaps;
	}

	private boolean isOutsideOfficeHours(final IBookingRequest booking, final LocalTime openingTime, final LocalTime closingTime) {
		return booking.getMeetingStartTime().isBefore(openingTime) || booking.getMeetingStartTime().isAfter(closingTime)
				|| booking.getMeetingEndDateTime().toLocalTime().isAfter(closingTime) || booking.getMeetingEndDateTime().toLocalTime().isBefore(openingTime);
	}

}
