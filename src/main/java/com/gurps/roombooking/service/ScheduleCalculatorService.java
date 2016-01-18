package com.gurps.roombooking.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import com.gurps.roombooking.domain.IBookingRequest;
import com.gurps.roombooking.domain.IBookingRequestBatch;

public interface ScheduleCalculatorService {
    
    static final String OUT_FILE_DELIM = " ";

    Map<LocalDate, SortedSet<IBookingRequest>> calculate(final IBookingRequestBatch batch);
}
