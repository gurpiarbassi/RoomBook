package com.gurps.roombooking;

import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Files.notExists;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gurps.roombooking.domain.BookingRequestScheduler;
import com.gurps.roombooking.service.IMeetingSchedulerService;
import com.gurps.roombooking.service.MeetingSchedulerService;

/**
 * 
 * @author Gurps Bassi gurpiar.bassi@gmail.com 
 * This is the main class where the action starts. Two arguments need to be passed into the main method
 * arg1 = input file path 
 * arg2 = output file path
 */
public class MeetingScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MeetingScheduler.class);
	
    private final IMeetingSchedulerService meetingSchedulerService;

    public MeetingScheduler(final IMeetingSchedulerService meetingSchedulerService){
		this.meetingSchedulerService = meetingSchedulerService;
    }
    
    public void run(){
    	 meetingSchedulerService.produceSchedule();
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("input path and output path should be specified.");
        } 
        
          Path inputFilePath = null;
          Path outputFilePath = null;
        	
        	try{
        		 inputFilePath = Paths.get(args[0]);
        		 outputFilePath = Paths.get(args[1]);
                 LOGGER.info("input file : {}",  inputFilePath);
                 LOGGER.info("output file : {} ", outputFilePath);
                 if(notExists(inputFilePath)){
                	 throw new IOException("Input file " + inputFilePath + " does not exist");
                 }
        	}catch(final Exception e){
        		throw new IOException(e);
        	}
        	
        	//TODO put behind factory
        	final MeetingScheduler meetingScheduler = new MeetingScheduler(new MeetingSchedulerService(newBufferedReader(inputFilePath),
        																   newBufferedWriter(outputFilePath),
        																   new BookingRequestScheduler()));
        	meetingScheduler.run();

    }
}
