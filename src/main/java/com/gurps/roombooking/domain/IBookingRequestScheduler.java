package com.gurps.roombooking.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedSet;

public interface IBookingRequestScheduler {
    
    static final String OUT_FILE_DELIM = " ";

    Map<LocalDate, SortedSet<IBookingRequest>> calculate(final IBookingRequestBatch batch);
}
