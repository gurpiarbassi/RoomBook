package com.gurps.roombooking.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface IBookingRequest {

	LocalDate getRequestDate();

	LocalTime getRequestTime();

	String getEmployeeId();

	LocalDate getMeetingDate();

	LocalTime getMeetingStartTime();

	LocalDateTime getMeetingEndDateTime();

	Integer getMeetingDuration();

}