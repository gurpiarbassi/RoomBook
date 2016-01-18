package com.gurps.roombooking.domain;

import java.time.LocalTime;
import java.util.Set;

public interface IBookingRequestBatch {

	LocalTime getOpeningTime();

	LocalTime getClosingTime();

	Set<IBookingRequest> getBookingRequests();

	boolean addBookingRequest(final IBookingRequest bookingRequest);
}
