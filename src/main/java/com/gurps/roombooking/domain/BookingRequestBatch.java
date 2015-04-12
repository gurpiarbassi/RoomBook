package com.gurps.roombooking.domain;

import java.time.LocalTime;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A Booking request batch consists of a header i.e. the opening and closing time
 * followed by a Set of booking requests.
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class BookingRequestBatch {

    private LocalTime openingTime;
    private LocalTime closingTime;
    
    private Set<BookingRequest> bookingRequests;
    
    public BookingRequestBatch(LocalTime openingTime, LocalTime closingTime){
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
    
    public LocalTime getOpeningTime() {
        return openingTime;
    }


    public LocalTime getClosingTime() {
        return closingTime;
    }


    public Set<BookingRequest> getBookingRequests() {
        return bookingRequests;
    }

    public void setBookingRequests(Set<BookingRequest> bookingRequests) {
        this.bookingRequests = bookingRequests;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

}
