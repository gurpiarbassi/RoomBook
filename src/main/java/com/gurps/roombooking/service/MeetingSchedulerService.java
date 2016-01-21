package com.gurps.roombooking.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.BookingRequestBatch;
import com.gurps.roombooking.domain.IBookingRequest;
import com.gurps.roombooking.domain.IBookingRequestBatch;
import com.gurps.roombooking.domain.IBookingRequestScheduler;

/**
 * Processes the given input schedule to produce an output schedule
 * This service makes use of two further services - ScheduleCalculatorService and SchedulePrinterService
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class MeetingSchedulerService implements IMeetingSchedulerService {

    private final Reader reader;
    private final Writer writer;
    private final IBookingRequestScheduler bookingRequestScheduler;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter REQ_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter START_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter COMPANY_HOURS_FMT = DateTimeFormatter.ofPattern("HHmm");

    private static final String IN_FILE_DELIM = " ";
    private static final String OUT_FILE_DELIM = " ";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingSchedulerService.class);
    
    
    private static final String ERROR_TXT = "INVALID INPUT";

    public MeetingSchedulerService(final Reader reader, final Writer writer, final IBookingRequestScheduler bookingRequestCalculator) {
        this.reader = reader;
        this.writer = writer;
        this.bookingRequestScheduler = bookingRequestCalculator; 
    }

    /**
     * Processes the input file and produce and domain representation of the
     * meeting request submission batch.
     * 
     * If any error occurs parsing the input file such as field formatting we treat
     * the whole input file as a bad file. In this case we write the constant MeetingScheduler.ERROR_TXT to the output file.
     */
    @Override
    public void produceSchedule() {
       LOGGER.info("Scheduling...");
        try {
            final IBookingRequestBatch batch = this.readInputFile();
            final Map<LocalDate, SortedSet<IBookingRequest>> output = bookingRequestScheduler.schedule(batch);
            print(output); //print the output
        } catch (final IOException e) {
        	LOGGER.error("Error processing schedule ", e);
            try {
                this.writeError();
            } catch (final IOException e2) {
                LOGGER.error("Unable to write error to file", e2);
            }

        }finally{
        	try{
        		reader.close();
            	writer.close();
        	}catch (final IOException e) {
            	LOGGER.error("Error closing readers and writers ", e);
        	}
        	
        }

    }

    /**
     * If any error occurs on processing the input file then write an error message
     * to the output file.
     * @throws IOException thrown when writing an error to the output file.
     */
    private void writeError() throws IOException {
    	LOGGER.error("Writing on Error");
        writer.write(ERROR_TXT);
    }

    /**
     * Reads the input file line by line.
     * The first line is taken as the company operating hours
     * The subsequent pairs of lines (i.e. 2 lines each) are treated as individual booking
     * requests.
     * @return a booking request batch or null if nothing to book
     * @throws IOException
     */
    private IBookingRequestBatch readInputFile() throws IOException {

        IBookingRequestBatch batch = null;

        try (Scanner scanner = new Scanner(reader)) {
            
            if (scanner.hasNextLine()) {
                batch = this.createBatch(scanner.nextLine());
                
                while (scanner.hasNextLine()) {
                	batch.addBookingRequest(parseRequest(scanner.nextLine(), scanner.nextLine()));
                }
            }
            return batch;
        }
    }

    /**
     * 
     * @param line the first line from the text input file.
     * @return BookingRequestBatch (a sort of parent object that encapsulates all the bookings)
     */
    private BookingRequestBatch createBatch(final String line) {
        final String[] hours = line.split(IN_FILE_DELIM);
        final LocalTime openingTime = LocalTime.parse(hours[0], COMPANY_HOURS_FMT);
        final LocalTime closingTime = LocalTime.parse(hours[1], COMPANY_HOURS_FMT);
        return new BookingRequestBatch(openingTime, closingTime);
    }

    private IBookingRequest parseRequest(final String firstLine, final String secondLine) {

        final String[] firstLineTokens = firstLine.split(IN_FILE_DELIM);
        final String[] secondLineTokens = secondLine.split(IN_FILE_DELIM);

        if (firstLineTokens == null || firstLineTokens.length != 3) {
            throw new IllegalArgumentException(
                    " Line must contain request date, request time and employee id");
        } else if (secondLineTokens == null || secondLineTokens.length != 3) {
            throw new IllegalArgumentException(
                    " Line must contain meeting date, start time and duration");
        }

        final LocalDate submissionDate = LocalDate.parse(firstLineTokens[0], DATE_FMT);
        final LocalTime submissionTime = LocalTime.parse(firstLineTokens[1], REQ_TIME_FMT);
        final String employeeNumber = firstLineTokens[2];
        
        if(employeeNumber == null || employeeNumber.length() == 0){
            throw new IllegalArgumentException("employee number not specified");
        }
        
        final LocalDate meetingDate = LocalDate.parse(secondLineTokens[0], DATE_FMT);
        final LocalTime meetingStartTime = LocalTime.parse(secondLineTokens[1], START_TIME_FMT);
        
        final int duration = Integer.parseInt(secondLineTokens[2]); 
        
        if(Math.signum(duration) <= 0){
            throw new IllegalArgumentException(
                    " Duration must be a positive integer");
        }

        final IBookingRequest bookingRequest =  BookingRequest.BookingRequestBuilder.aBookingRequest(submissionDate,
                submissionTime).withMeetingDate(meetingDate).withMeetingStartTime(meetingStartTime)
                .withMeetingDuration(duration).withEmployee(employeeNumber).build();

        return bookingRequest;
    }
    

    
   
    
    /**
     * @Param bookings All the successful booking requests
     * Formats the bookings and prints to a file. 
     */
    public void print(final Map<LocalDate, SortedSet<IBookingRequest>> bookings) throws IOException {
        final StringBuilder outputBuilder = new StringBuilder();
        final Set<Entry<LocalDate, SortedSet<IBookingRequest>>> entries = bookings.entrySet();
        final Iterator<Entry<LocalDate, SortedSet<IBookingRequest>>> meetingDaysIterator = entries.iterator();
        while(meetingDaysIterator.hasNext()){
            final Entry<LocalDate, SortedSet<IBookingRequest>> entry = meetingDaysIterator.next();
            final LocalDate meetingDate = entry.getKey();
            outputBuilder.append(meetingDate.toString() + System.getProperty("line.separator"));
            
            final Iterator<IBookingRequest> it = entry.getValue().iterator();
            while(it.hasNext()){
                final IBookingRequest bookingRequest = it.next();
                outputBuilder.append(bookingRequest.getMeetingStartTime())
                              .append(OUT_FILE_DELIM)
                              .append(bookingRequest.getMeetingEndDateTime().toLocalTime())
                              .append(OUT_FILE_DELIM)
                              .append(bookingRequest.getEmployeeId());
                if(it.hasNext()){
                    outputBuilder.append(System.getProperty("line.separator"));
                }
            }
            if(meetingDaysIterator.hasNext()){
                outputBuilder.append(System.getProperty("line.separator"));
            }
        }
        writer.write(outputBuilder.toString());
    }

}
