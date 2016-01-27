package com.gurps.roombooking.domain;

import java.time.LocalTime;
import java.util.List;

public interface IBookingRequestBatch {

	LocalTime getOpeningTime();

	LocalTime getClosingTime();

	List<IBookingRequest> getBookingRequests();

	boolean addBookingRequest(final IBookingRequest bookingRequest);
}
