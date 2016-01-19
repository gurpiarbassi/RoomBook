package com.gurps.roombooking;

import java.io.File;
import java.io.IOException;

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
        
          File inputFile = null;
          File outputFile = null;
        	
        	try{
        		 inputFile = new File(args[0]);
                 outputFile = new File(args[1]);
                 System.out.println("input file : " + inputFile);
                 System.out.println("output file : " + outputFile);
                 if(!inputFile.exists()){
                	 throw new IOException("Input file " + inputFile + " does not exist");
                 }
        	}catch(final Exception e){
        		throw new IOException(e);
        	}
        	
        	final MeetingScheduler meetingScheduler = new MeetingScheduler(new MeetingSchedulerService(inputFile, outputFile));
        	meetingScheduler.run();

    }
}
