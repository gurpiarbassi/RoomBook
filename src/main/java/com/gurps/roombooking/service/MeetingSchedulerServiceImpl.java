package com.gurps.roombooking.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import com.gurps.roombooking.domain.BookingRequest;
import com.gurps.roombooking.domain.BookingRequestBatch;
import com.gurps.roombooking.domain.IBookingRequest;
import com.gurps.roombooking.domain.IBookingRequestBatch;

/**
 * Processes the given input schedule to produce an output schedule
 * This service makes use of two further services - ScheduleCalculatorService and SchedulePrinterService
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com
 *
 */
public class MeetingSchedulerServiceImpl implements MeetingSchedulerService {

    private final String inputFilePath;
    private final String outputFilePath;

    
    private final ScheduleCalculatorService scheduleOutputService;
    private final SchedulePrinterService printerService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter REQ_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter START_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter COMPANY_HOURS_FMT = DateTimeFormatter.ofPattern("HHmm");

    private static final String IN_FILE_DELIM = " ";
    
    
    private static final String ERROR_TXT = "INVALID INPUT";

    /**
     * Makes use of two child services - namely the ScheduleCalculatorService and SchedulePrinterService
     * @param inputFilePath the input file path
     * @param outputFilePath the output file path
     */
    public MeetingSchedulerServiceImpl(final String inputFilePath, final String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        
        //TODO use DI to shield concrete implementation from client to allow future flexiblity in scheduling & printing rules
        this.scheduleOutputService = new BasicCalculatorService(); 
        this.printerService = new FilePrinterServiceImpl(outputFilePath);
    }

    /**
     * Processes the input file and produce and domain representation of the
     * meeting request submisson batch.
     * 
     * If any error occurs parsing the input file such as field formatting we treat
     * the whole input file as a bad file. In this case we write the constant MeetingScheduler.ERROR_TXT to the output file.
     */
    @Override
    public void produceSchedule() {
        System.out.println("Scheduling...");
        try {
            final IBookingRequestBatch batch = this.readInputFile();
            final Map<LocalDate, SortedSet<IBookingRequest>> output = this.scheduleOutputService.calculate(batch);
            this.printerService.print(output); //print the output
        } catch (final IOException e) {
            e.printStackTrace();
            try {
                this.writeError();
            } catch (final Exception e2) {
                e2.printStackTrace();
                System.err.println("Unable to write error to file");
            }

        }

    }

    /**
     * If any error occurs on processing the input file then write an error message
     * to the output file.
     * @throws IOException thrown when writing an error to the output file.
     */
    private void writeError() throws IOException {
        System.out.println("Writing on Error");
        Files.write(Paths.get(this.outputFilePath), ERROR_TXT.getBytes());
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

        final SortedSet<BookingRequest> bookingRequests = new TreeSet<>();
        IBookingRequestBatch batch = null;
        
        try (Scanner scanner = new Scanner(Paths.get(this.inputFilePath), Charset.defaultCharset()
                .name())) {
            
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
}
