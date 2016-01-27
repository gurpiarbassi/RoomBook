package com.gurps.roombooking.domain;

import java.util.Comparator;

public class ScheduledMeetingComparator implements Comparator<IBookingRequest> {

	@Override
	public int compare(final IBookingRequest first, final IBookingRequest second) {

		final Comparator<IBookingRequest> comparator = Comparator.comparing(IBookingRequest::getRequestDate)
																.thenComparing(IBookingRequest::getRequestTime)
																.thenComparing(IBookingRequest::getMeetingDate)
																.thenComparing(IBookingRequest::getMeetingStartTime)
																.thenComparing(IBookingRequest::getMeetingEndDateTime);
		return comparator.compare(first, second);
	}
}
