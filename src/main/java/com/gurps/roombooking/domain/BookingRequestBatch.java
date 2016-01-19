package com.gurps.roombooking.domain;

import static java.util.Collections.unmodifiableSortedSet;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.time.LocalTime;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public final class BookingRequestBatch implements IBookingRequestBatch {

	private final LocalTime openingTime;
	private final LocalTime closingTime;

	private final SortedSet<IBookingRequest> bookingRequests;

	public BookingRequestBatch(final LocalTime openingTime, final LocalTime closingTime) {
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.bookingRequests = new TreeSet<>();
	}

	@Override
	public LocalTime getOpeningTime() {
		return openingTime;
	}

	@Override
	public LocalTime getClosingTime() {
		return closingTime;
	}
	
	@Override
	public boolean addBookingRequest(final IBookingRequest bookingRequest){
		return bookingRequests.add(bookingRequest);
	}

	@Override
	public SortedSet<IBookingRequest> getBookingRequests() {
		return unmodifiableSortedSet(this.bookingRequests);
	}

	@Override
	public String toString() {
		return reflectionToString(this, MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object that) {
		return reflectionEquals(this, that);
	}

}
