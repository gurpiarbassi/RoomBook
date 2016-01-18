package com.gurps.roombooking.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Encapsulates a individual booking request submission
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class BookingRequest implements Comparable<BookingRequest> {

    private final LocalDate requestDate;
    private final LocalTime requestTime;
    private final String employeeId;

    private final LocalDate meetingDate;
    private final LocalTime meetingStartTime;
    private final int meetingDuration;

    /**
     * Assuming meetings can go into the next day if company hours permit
     * we need to model the meeting end time as a date/time.
     */    
    private LocalDateTime meetingEndDateTime;

    private BookingRequest(final BookingRequestBuilder builder) {
	this.requestDate = builder.requestDate;
	this.requestTime = builder.requestTime;
	this.employeeId = builder.employeeId;
	this.meetingDate = builder.meetingDate;
	this.meetingStartTime = builder.meetingStartTime;
	this.meetingDuration = builder.meetingDuration;
	this.meetingEndDateTime = builder.meetingEndDateTime;
    }

    public LocalDate getRequestDate() {
	return requestDate;
    }

    public LocalTime getRequestTime() {
	return requestTime;
    }

    public String getEmployeeId() {
	return employeeId;
    }

    public LocalDate getMeetingDate() {
	return meetingDate;
    }

    public LocalTime getMeetingStartTime() {
	return meetingStartTime;
    }

    public LocalDateTime getMeetingEndDateTime() {
	LocalDateTime start = LocalDateTime.of(meetingDate, meetingStartTime);
	this.meetingEndDateTime = start.plus(getMeetingDuration(),
		ChronoUnit.HOURS);
	return meetingEndDateTime;
    }

    public int getMeetingDuration() {
	return meetingDuration;
    }

    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (obj == this) {
	    return true;
	}
	if (obj.getClass() != getClass()) {
	    return false;
	}
	BookingRequest rhs = (BookingRequest) obj;
	return new EqualsBuilder().appendSuper(super.equals(obj))
		.append(getRequestDate(), rhs.getRequestDate())
		.append(getRequestTime(), rhs.getRequestTime()).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(getRequestDate())
		.append(getRequestTime()).toHashCode();
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this,
		ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    /**
     * Constraint: 'Bookings must be processed in the chronological order in which they were submitted'.
     * Default to ordering the bookings by request submission date/time
     * @param that BookingRequest to compare.
     * @return 
     *  0 if the booking requests were submitted at the same time (Although business rules gurantee this wont happen)
     *  1 if this booking request was submitted after that booking request
     *  -1 if this booking request was submitted before that booking request
     * 
     */
    public int compareTo(BookingRequest that) {

	return new CompareToBuilder()
		.append(this.getRequestDate(), that.getRequestDate())
		.append(this.getRequestTime(), that.getRequestTime())
		.toComparison();
    }

    /**
     * Builder class to allow construction of a immutable BookingRequest
     * and avoid telescoping constructor.
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

	private int meetingDuration;

	public BookingRequestBuilder(LocalDate requestDate,
		LocalTime requestTime) {
	    this.requestDate = requestDate;
	    this.requestTime = requestTime;
	}

	public BookingRequestBuilder employee(String employeeId) {
	    this.employeeId = employeeId;
	    return this;
	}

	public BookingRequestBuilder meetingDate(LocalDate meetingDate) {
	    this.meetingDate = meetingDate;
	    return this;
	}

	public BookingRequestBuilder meetingStart(LocalTime meetingStart) {
	    this.meetingStartTime = meetingStart;
	    return this;
	}

	public BookingRequestBuilder duration(int duration) {
	    this.meetingDuration = duration;
	    return this;
	}

	private BookingRequestBuilder meetingEnd(LocalDateTime meetingEnd) {
	    this.meetingEndDateTime = meetingEnd;
	    return this;
	}

	/**
	 * Builds a BookingRequest object and automatically computes
	 * the meeting end date/time for you.
	 * @return
	 */
	public BookingRequest build() {
	    LocalDateTime start = LocalDateTime.of(meetingDate,
		    meetingStartTime);
	    return new BookingRequest(this.meetingEnd(start.plus(
		    meetingDuration, ChronoUnit.HOURS)));
	}

    }
}
