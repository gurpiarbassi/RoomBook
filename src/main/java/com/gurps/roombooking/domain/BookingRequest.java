package com.gurps.roombooking.domain;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingRequest implements IBookingRequest {

	private final LocalDate requestDate;
	private final LocalTime requestTime;
	private final String employeeId;

	private final LocalDate meetingDate;
	private final LocalTime meetingStartTime;
	private final Integer meetingDuration;

	/**
	 * Assuming meetings can go into the next day if company hours permit we
	 * need to model the meeting end time as a date/time.
	 */
	private final LocalDateTime meetingEndDateTime;

	private BookingRequest(final BookingRequestBuilder builder) {
		this.requestDate = builder.requestDate;
		this.requestTime = builder.requestTime;
		this.employeeId = builder.employeeId;
		this.meetingDate = builder.meetingDate;
		this.meetingStartTime = builder.meetingStartTime;
		this.meetingDuration = builder.meetingDuration;
		this.meetingEndDateTime = builder.meetingEndDateTime;
	}

	@Override
	public LocalDate getRequestDate() {
		return requestDate;
	}

	@Override
	public LocalTime getRequestTime() {
		return requestTime;
	}

	@Override
	public String getEmployeeId() {
		return employeeId;
	}

	@Override
	public LocalDate getMeetingDate() {
		return meetingDate;
	}

	@Override
	public LocalTime getMeetingStartTime() {
		return meetingStartTime;
	}

	@Override
	public LocalDateTime getMeetingEndDateTime() {
		return meetingEndDateTime;
	}

	@Override
	public Integer getMeetingDuration() {
		return meetingDuration;
	}

	@Override
	public boolean equals(final Object that) {
		return reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return reflectionHashCode(this);
	}
	
	@Override
	public String toString() {
		return reflectionToString(this, MULTI_LINE_STYLE);
	}
	
	/**
	 * Builder class to allow construction of a immutable BookingRequest and
	 * avoid telescoping constructor.
	 * 
	 * Note: The meeting end time will be automatically computed by the builder.
	 * 
	 */
	public static class BookingRequestBuilder {
		private final LocalDate requestDate;
		private final LocalTime requestTime;
		private String employeeId;

		private LocalDate meetingDate;
		private LocalTime meetingStartTime;
		private LocalDateTime meetingEndDateTime;

		private Integer meetingDuration;

		private BookingRequestBuilder(final LocalDate requestDate, final LocalTime requestTime) {
			this.requestDate = requestDate;
			this.requestTime = requestTime;
		}
		
		public static BookingRequestBuilder aBookingRequest(final LocalDate requestDate, final LocalTime requestTime){
			return new BookingRequestBuilder(requestDate, requestTime);
		}

		public BookingRequestBuilder withEmployee(final String employeeId) {
			this.employeeId = employeeId;
			return this;
		}

		public BookingRequestBuilder withMeetingDate(final LocalDate meetingDate) {
			this.meetingDate = meetingDate;
			return this;
		}

		public BookingRequestBuilder withMeetingStartTime(final LocalTime meetingStartTime) {
			this.meetingStartTime = meetingStartTime;
			return this;
		}

		public BookingRequestBuilder withMeetingDuration(final Integer duration) {
			this.meetingDuration = duration;
			return this;
		}

		private BookingRequestBuilder withMeetingEndDateTime(final LocalDateTime meetingEnd) {
			this.meetingEndDateTime = meetingEnd;
			return this;
		}
		
		private void validate(){
			notNull(meetingDate);
			notNull(meetingStartTime);
			notNull(meetingDuration);
			notEmpty(employeeId);
		}

		/**
		 * Builds a BookingRequest object and automatically computes the meeting
		 * end date/time for you.
		 */
		public BookingRequest build() {
			validate();
			
			final LocalDateTime start = LocalDateTime.of(meetingDate, meetingStartTime);
			return new BookingRequest(withMeetingEndDateTime(start.plus(meetingDuration, HOURS)));
		}

	}
}
