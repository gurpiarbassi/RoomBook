package com.gurps.roombooking.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.BookingRequestBatch;

public interface ScheduleCalculatorService {
    
    static final String OUT_FILE_DELIM = " ";

    Map<LocalDate, SortedSet<BookingRequest>> calculate(BookingRequestBatch batch);
}
